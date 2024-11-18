package pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.github;

import io.vavr.control.Try;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.OAuth2Provider;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.OAuth2ProviderResult;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.OAuth2ProviderSuperclass;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GithubOAuth2Provider extends OAuth2ProviderSuperclass implements OAuth2Provider {
    private static final String REGISTRATION_ID = "github";

    public GithubOAuth2Provider(OAuth2RestTemplate oAuth2RestTemplate) {
        super(oAuth2RestTemplate);
    }

    @Override
    public String getRegistrationId() {
        return REGISTRATION_ID;
    }

    @Override
    public boolean isApply(String registrationId) {
        return getRegistrationId().equals(registrationId);
    }

    @Override
    public OAuth2ProviderResult retrieve(OAuth2UserRequest userRequest) {
        Map<String, Object> attributes = super.getAttributes(userRequest);
        attributes.put("registrationId", REGISTRATION_ID);
        fixEmailNull(attributes, userRequest);
        return new GithubOAuth2ProviderResult(attributes);
    }

    private void fixEmailNull(Map<String, Object> attributes, OAuth2UserRequest userRequest) {
        final var userInfoUri = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUri();

        final var typeReference = new ParameterizedTypeReference<List<Email>>() {
        };
        Try.of(() -> this.oAuth2RestTemplate.exchange(userInfoUri + "/emails", typeReference, userRequest).getBody())
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
