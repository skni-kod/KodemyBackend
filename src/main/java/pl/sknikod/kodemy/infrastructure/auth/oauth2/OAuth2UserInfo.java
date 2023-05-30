package pl.sknikod.kodemy.infrastructure.auth.oauth2;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public abstract class OAuth2UserInfo {
    public Map<String, Object> attributes;

    public abstract String getPrincipalId();

    public abstract String getUsername();

    public abstract String getEmail();

    public abstract String getPhoto();
}