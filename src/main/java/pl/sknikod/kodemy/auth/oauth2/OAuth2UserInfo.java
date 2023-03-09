package pl.sknikod.kodemy.auth.oauth2;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public abstract class OAuth2UserInfo {
    public Map<String, Object> attributes;

    public abstract String getPrincipalId();

    public abstract String getUsername();

    public abstract String getEmail();

    public abstract String getPhoto();
}