package pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.github;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.OAuth2Provider;

import java.util.Map;

@Component
public class GithubOAuth2Provider extends OAuth2Provider {
    private static final String REGISTRATION_ID = "github";

    public GithubOAuth2Provider(GithubOAuth2Stage githubOAuth2Stage) {
        super(githubOAuth2Stage);
    }

    @Override
    public String getRegistrationId() {
        return REGISTRATION_ID;
    }

    @Override
    public boolean supports(String registrationId) {
        return REGISTRATION_ID.equals(registrationId);
    }

    @Override
    public GithubUser retrieveUser(RestTemplate oAuth2RestTemplate, OAuth2UserRequest userRequest) {
        Map<String, Object> attributes = oAuth2Stage.retrieveAttributes(userRequest);
        return new GithubUser(attributes);
    }

    public static class GithubUser extends User {
        public GithubUser(Map<String, Object> attributes) {
            super(attributes);
        }

        @Override
        public String getProvider() {
            return REGISTRATION_ID;
        }

        @Override
        public String getPrincipalId() {
            return attributes.get("id").toString();
        }

        @Override
        public String getUsername() {
            return attributes.get("login").toString();
        }

        @Override
        public String getEmail() {
            return attributes.get("email").toString();
        }

        @Override
        public String getPhoto() {
            return attributes.get("avatar_url").toString();
        }
    }
}
