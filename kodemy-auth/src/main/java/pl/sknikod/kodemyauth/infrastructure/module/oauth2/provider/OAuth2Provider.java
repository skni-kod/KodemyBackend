package pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.github.GithubOAuth2Provider;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2RestOperations;

import java.util.Map;

public interface OAuth2Provider {
    String getRegistrationId();
    boolean supports(String registrationId);

    GithubOAuth2Provider.GithubUser retrieve(
            OAuth2RestOperations oAuth2RestOperations, OAuth2UserRequest userRequest
    );

    @Getter
    @Component
    @AllArgsConstructor
    abstract class User {
        protected final Map<String, Object> attributes;

        public abstract String getProvider();

        public abstract String getPrincipalId();

        public abstract String getUsername();

        public abstract String getEmail();

        public abstract String getPhoto();
    }
}
