package pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.github;

import io.vavr.control.Try;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.OAuth2Stage;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2RestOperations;

import java.util.List;

@Slf4j
@Component
public class GithubOAuth2Stage extends OAuth2Stage {
    public GithubOAuth2Stage(OAuth2RestOperations oAuth2RestOperations) {
        super(oAuth2RestOperations);
    }

    @Override
    protected void addAdditionalProcesses(@NonNull OAuth2UserRequest userRequest) {
        processFixEmailNull(userRequest);
    }

    private void processFixEmailNull(OAuth2UserRequest userRequest) {
        final var userInfoUri = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUri();

        Try.of(() -> this.oAuth2RestOperations.exchange(userInfoUri + "/emails", new ParameterizedTypeReference<List<Email>>() {
                }, userRequest).getBody())
                .onSuccess(unused -> log.info("Successfully retrieved emails"))
                .map(emails -> emails.stream().filter(e -> e.primary).findFirst().orElse(null))
                .peek(e -> this.attributes.put("email", e.email));
    }

    @Data
    private static class Email {
        private String email;
        private boolean primary;
        private boolean verified;
    }
}