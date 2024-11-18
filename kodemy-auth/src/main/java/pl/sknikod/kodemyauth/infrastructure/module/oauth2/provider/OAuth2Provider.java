package pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

public interface OAuth2Provider {
    String getRegistrationId();
    boolean isApply(String registrationId);
    OAuth2ProviderResult retrieve(OAuth2UserRequest userRequest);
}
