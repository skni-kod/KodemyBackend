package pl.sknikod.kodemy.infrastructure.auth.oauth2;

import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {
    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
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
        return String.valueOf(getAttributes().get("login"));
    }

    @Override
    public String getPhoto() {
        return getAttributes().get("avatar_url").toString();
    }
}