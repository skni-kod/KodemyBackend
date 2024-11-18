package pl.sknikod.kodemyauth.infrastructure.module.oauth2.processor.github;

import pl.sknikod.kodemyauth.infrastructure.module.oauth2.processor.OAuth2ProcessorResult;

import java.util.Map;

public class GithubOAuth2ProcessorResult extends OAuth2ProcessorResult {
    public GithubOAuth2ProcessorResult(Map<String, Object> attributes) {
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
