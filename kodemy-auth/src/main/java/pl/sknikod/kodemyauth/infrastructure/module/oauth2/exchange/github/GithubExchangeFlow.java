package pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange.github;

import io.vavr.control.Try;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.sknikod.kodemyauth.configuration.WebConfiguration;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange.ProviderExchangeFlow;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange.ProviderUser;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange.Registration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GithubExchangeFlow extends ProviderExchangeFlow {
    public GithubExchangeFlow(@Qualifier(WebConfiguration.OAUTH2_REST_TEMPLATE) RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public Registration getRegistration() {
        return Registration.github;
    }

    @Override
    public boolean isApply(String registrationId) {
        return getRegistration().getId().equals(registrationId);
    }

    @Override
    public ProviderUser exchange(ClientRegistrationRepository repository, String code) {
        ClientRegistration clientRegistration = repository.findByRegistrationId(getRegistration().getId());
        Map<String, Object> attributes = initNewAttributesMap(clientRegistration);
        AccessToken accessToken = super.postForAccessToken(clientRegistration, code);
        attributes.putAll(super.getUserAttributes(clientRegistration, accessToken));
        fixEmailNull(attributes, clientRegistration, accessToken);
        return new GithubUser(attributes);
    }

    private void fixEmailNull(Map<String, Object> attributes, ClientRegistration clientRegistration, AccessToken accessToken) {
        final var userInfoUri = clientRegistration
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUri();

        var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(accessToken.getAccessToken());

        final var typeReference = new ParameterizedTypeReference<List<Email>>() {
        };
        Try.of(() -> restTemplate.exchange(userInfoUri + "/emails", HttpMethod.GET, new HttpEntity<>(headers), typeReference))
                .filter(response -> response.getStatusCode().is2xxSuccessful())
                .map(HttpEntity::getBody)
                .onSuccess(unused -> log.info("Successfully retrieved emails"))
                .map(emails -> emails.stream().filter(e -> e.primary).findFirst().orElse(null))
                .peek(e -> attributes.put("email", e.email));
    }

    @Data
    private static class Email {
        private String email;
        private boolean primary;
        private boolean verified;
    }
}
