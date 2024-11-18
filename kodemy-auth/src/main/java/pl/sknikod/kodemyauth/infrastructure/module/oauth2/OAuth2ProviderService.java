package pl.sknikod.kodemyauth.infrastructure.module.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.OAuth2Provider;

import java.util.List;

@Service
public class OAuth2ProviderService {
    private final List<OAuth2Provider> oAuth2Providers;
    private final String authorizeEndpoint;

    public OAuth2ProviderService(
            List<OAuth2Provider> oAuth2Providers,
            @Value("${app.security.oauth2.endpoints.authorize}") String authorizeEndpoint
    ) {
        this.oAuth2Providers = oAuth2Providers;
        this.authorizeEndpoint = authorizeEndpoint;
    }

    public List<ProviderResponse> getProviders() {
        return oAuth2Providers
                .stream()
                .map(oAuth2Provider -> new ProviderResponse(oAuth2Provider.getRegistrationId(), authorizeEndpoint))
                .toList();
    }

    @lombok.Value
    public static class ProviderResponse {
        String provider;
        String authorize;

        public ProviderResponse(String provider, String authorizeEndpoint) {
            this.provider = provider;
            this.authorize = authorizeEndpoint + "/" + provider;
        }
    }
}
