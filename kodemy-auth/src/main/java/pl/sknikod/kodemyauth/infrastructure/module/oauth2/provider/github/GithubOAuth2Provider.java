package pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.github;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.OAuth2Provider;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2RestOperations;

import java.util.Map;

@Component
public class GithubOAuth2Provider implements OAuth2Provider {
    private static final String REGISTRATION_ID = "github";

    @Override
    public String getRegistrationId() {
        return REGISTRATION_ID;
    }

    @Override
    public boolean supports(String registrationId) {
        return REGISTRATION_ID.equals(registrationId);
    }

    @Override
    public GithubUser retrieve(
            OAuth2RestOperations oAuth2RestOperations, OAuth2UserRequest userRequest) {
        return new GithubUser(new GithubOAuth2Workflow(oAuth2RestOperations, userRequest).retrieve());
    }

    public static class GithubUser extends OAuth2Provider.User {
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
