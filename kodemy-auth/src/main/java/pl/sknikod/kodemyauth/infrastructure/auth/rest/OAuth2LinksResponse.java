package pl.sknikod.kodemyauth.infrastructure.auth.rest;

import lombok.Value;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Provider;

import java.util.List;

@Value
public class OAuth2LinksResponse {
    String logoutUri;
    List<ProviderDetails> providers;

    @Value
    public static class ProviderDetails {
        Provider.ProviderType name;
        String loginUri;
    }
}
