package pl.sknikod.kodemy.infrastructure.auth.oauth2;

import pl.sknikod.kodemy.infrastructure.common.entity.UserProviderType;

import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {
    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public UserProviderType getProvider() {
        return UserProviderType.GITHUB;
    }

    @Override
    public String getPrincipalId() {
        return getAttributes().get("id").toString();
    }

    @Override
    public String getUsername() {
        return getAttributes().get("login").toString();
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getPhoto() {
        return getAttributes().get("avatar_url").toString();
    }
}