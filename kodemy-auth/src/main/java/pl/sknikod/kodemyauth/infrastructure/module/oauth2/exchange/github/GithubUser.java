package pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange.github;

import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange.ProviderUser;

import java.util.Map;

public class GithubUser extends ProviderUser {
    public GithubUser(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getRegistrationId() {
        return attributes
                .get(DefaultServerOAuth2AuthorizationRequestResolver.DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME)
                .toString();
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
