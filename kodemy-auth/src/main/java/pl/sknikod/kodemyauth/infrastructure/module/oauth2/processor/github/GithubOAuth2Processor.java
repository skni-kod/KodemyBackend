package pl.sknikod.kodemyauth.infrastructure.module.oauth2.processor.github;

import io.vavr.control.Try;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.processor.OAuth2Processor;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.processor.OAuth2ProcessorResult;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
public class GithubOAuth2Processor extends OAuth2Processor {
    public GithubOAuth2Processor(OAuth2RestTemplate oAuth2RestTemplate) {
        super(oAuth2RestTemplate);
    }

    @Override
    public OAuth2ProcessorResult process(OAuth2UserRequest userRequest) {
        Map<String, Object> attributes = super.getAttributes(userRequest);
        fixEmailNull(attributes, userRequest);
        return new GithubOAuth2ProcessorResult(attributes);
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
