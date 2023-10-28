package pl.sknikod.kodemyauth.infrastructure.auth.oauth2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Provider;

import java.util.Map;

@Component
@AllArgsConstructor
public abstract class OAuth2UserInfo {
    @Getter
    private final Map<String, Object> attributes;

    public abstract Provider.ProviderType getProvider();

    public abstract String getPrincipalId();

    public abstract String getUsername();

    public abstract String getEmail();

    public abstract String getPhoto();
}