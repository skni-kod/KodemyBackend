package pl.sknikod.kodemyauth.infrastructure.module.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange.Registration;

import java.util.Arrays;
import java.util.List;

@Service
public class OAuth2ProvidersService {
    private final String gatewayBaseUrl;
    private final String authorizeEndpoint;

    public OAuth2ProvidersService(
            @Value("${app.security.oauth2.baseUrl.gateway}") String gatewayBaseUrl,
            @Value("${app.security.oauth2.endpoint.authorize}") String authorizeEndpoint
    ) {
        this.gatewayBaseUrl = gatewayBaseUrl;
        this.authorizeEndpoint = authorizeEndpoint;
    }

    public List<ProviderResponse> getProviders() {
        return Arrays.stream(Registration.values())
                .map(registration -> new ProviderResponse(registration.getId(), gatewayBaseUrl, authorizeEndpoint))
                .toList();
    }

    @lombok.Value
    public static class ProviderResponse {
        String provider;
        String authorize;

        public ProviderResponse(String registrationId, String gatewayBaseUrl, String authorizeEndpoint) {
            this.provider = registrationId;
            this.authorize = gatewayBaseUrl + authorizeEndpoint + "/" + registrationId;
        }
    }
}
