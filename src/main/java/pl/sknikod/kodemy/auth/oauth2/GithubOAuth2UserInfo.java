package pl.sknikod.kodemy.auth.oauth2;

import pl.sknikod.kodemy.auth.oauth2.OAuth2UserInfo;

import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {
    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
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
        return null;
    }

    @Override
    public String getPhoto() {
        return attributes.get("avatar_url").toString();
    }
}