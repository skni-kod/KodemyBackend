package pl.sknikod.kodemyauth.infrastructure.module.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Slf4j
public class OAuth2AuthorizeRequestResolver implements OAuth2AuthorizationRequestResolver {
    private final OAuth2AuthorizationRequestResolver delegate;

    public OAuth2AuthorizeRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository, String authorizeBaseUri
    ) {
        this.delegate =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, authorizeBaseUri);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        return null;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        return delegate.resolve(request, clientRegistrationId);
    }
}
