package pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.github;

import io.vavr.control.Try;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.OAuth2Workflow;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2RestOperations;

import java.util.List;

@Slf4j
public class GithubOAuth2Workflow extends OAuth2Workflow {
    private static final ParameterizedTypeReference<List<Email>> EMAILS_TYPE_REF =
            new ParameterizedTypeReference<>() {
            };

    public GithubOAuth2Workflow(
            @NonNull OAuth2RestOperations oAuth2RestOperations,
            @NonNull OAuth2UserRequest userRequest) {
        super(oAuth2RestOperations, userRequest);
    }

    @Override
    protected void addPostProcesses() {
        processFixEmailNull();
    }

    private void processFixEmailNull() {
        final var userInfoUri = this.userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUri();

        Try.of(() -> this.restOperations.exchange(
                        userInfoUri + "/emails", EMAILS_TYPE_REF, userRequest).getBody())
                .onSuccess(unused -> log.info("Successfully retrieved emails"))
                .map(emails ->
                        emails.stream().filter(e -> e.primary).findFirst().orElse(null))
                .peek(e -> this.attributes.put("email", e.email));
    }

    @Data
    private static class Email {
        private String email;
        private boolean primary;
        private boolean verified;
    }
}