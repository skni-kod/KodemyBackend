package pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.github;

import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.OAuth2ProviderResult;

import java.util.Map;

public class GithubOAuth2ProviderResult extends OAuth2ProviderResult {
    public GithubOAuth2ProviderResult(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getRegistrationId() {
        return attributes.get("registration_id").toString();
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
