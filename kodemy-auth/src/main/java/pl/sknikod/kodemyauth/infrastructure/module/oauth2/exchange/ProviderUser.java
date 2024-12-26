package pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public abstract class ProviderUser {
    protected static final String REGISTRATION_ID_KEY = "registration_id";

    protected final Map<String, Object> attributes;

    public abstract String getRegistrationId();

    public abstract String getPrincipalId();

    public abstract String getUsername();

    public abstract String getEmail();

    public abstract String getPhoto();
}
