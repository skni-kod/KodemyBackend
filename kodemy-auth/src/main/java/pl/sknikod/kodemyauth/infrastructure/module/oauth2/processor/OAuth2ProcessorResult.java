package pl.sknikod.kodemyauth.infrastructure.module.oauth2.processor;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public abstract class OAuth2ProcessorResult {
    protected final Map<String, Object> attributes;

    public abstract String getRegistrationId();

    public abstract String getPrincipalId();

    public abstract String getUsername();

    public abstract String getEmail();

    public abstract String getPhoto();
}