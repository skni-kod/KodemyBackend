package pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.web.client.RestTemplate;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.github.GithubOAuth2Provider;

import java.util.Map;

@RequiredArgsConstructor
public abstract class OAuth2Provider {
    protected final Stage oAuth2Stage;

    public abstract String getRegistrationId();

    public abstract boolean supports(String registrationId);

    public abstract GithubOAuth2Provider.GithubUser retrieveUser(RestTemplate oAuth2RestTemplate, OAuth2UserRequest userRequest);

    @Getter
    @AllArgsConstructor
    public static abstract class User {
        protected final Map<String, Object> attributes;

        public abstract String getProvider();

        public abstract String getPrincipalId();

        public abstract String getUsername();

        public abstract String getEmail();

        public abstract String getPhoto();
    }

    public interface Stage {
        Map<String, Object> retrieveAttributes(@NonNull OAuth2UserRequest userRequest);
    }
}
