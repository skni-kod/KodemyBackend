package pl.sknikod.kodemyauth.infrastructure.oauth2.userinfo;

import pl.sknikod.kodemyauth.infrastructure.common.entity.Provider;

import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {
    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public Provider.ProviderType getProvider() {
        return Provider.ProviderType.GITHUB;
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