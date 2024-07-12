package pl.sknikod.kodemyauth.infrastructure.module.oauth2;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;

import static org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver.DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME;

public class OAuth2SessionAuthRequestRepository implements
        AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    private static final String AUTH_REQ_NAME =
            OAuth2SessionAuthRequestRepository.class.getName() + ".AUTH_REQ";
    private final OAuth2AuthorizationRequestUtil oAuth2AuthorizationRequestUtil;

    public OAuth2SessionAuthRequestRepository(
            ClientRegistrationRepository clientRegistrationRepository, String callbackFullUri) {
        this.oAuth2AuthorizationRequestUtil = new OAuth2AuthorizationRequestUtil(
                clientRegistrationRepository, new AntPathRequestMatcher(callbackFullUri)
        );
    }

    @Override
    @Nullable
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return oAuth2AuthorizationRequestUtil.resolve(request);
    }

    @Override
    public void saveAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response
    ) {
        // unnecessarily save anything in the callback
    }

    @Override
    @Nullable
    public OAuth2AuthorizationRequest removeAuthorizationRequest(
            HttpServletRequest request, HttpServletResponse response) {
        // unnecessarily remove anything in the callback
        return this.loadAuthorizationRequest(request);
    }

    private record OAuth2AuthorizationRequestUtil(
            ClientRegistrationRepository clientRegistrationRepository,
            AntPathRequestMatcher callbackMatcher
    ) {
        public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
            final var registrationId = resolveRegistrationId(request);
            if (registrationId == null) return null;
            final var clientRegistration = findByRegistrationId(registrationId);
            OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.authorizationCode();
            builder.clientId(clientRegistration.getClientId())
                    .authorizationUri(request.getRequestURI())
                    .scopes(clientRegistration.getScopes())
                    .state(getStateParameter(request))
                    .attributes((attrs) -> attrs
                            .put(OAuth2ParameterNames.REGISTRATION_ID, clientRegistration.getRegistrationId()));
            return builder.build();
        }

        @Nullable
        private String resolveRegistrationId(HttpServletRequest request) {
            if (!this.callbackMatcher.matches(request))
                return null;
            return this.callbackMatcher.matcher(request)
                    .getVariables()
                    .get(DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME);
        }

        private ClientRegistration findByRegistrationId(String registrationId) {
            var clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);
            if (clientRegistration == null) {
                throw new IllegalArgumentException("Invalid client registration: " + registrationId);
            }
            return clientRegistration;
        }

        private String getStateParameter(HttpServletRequest request) {
            return Arrays.stream(request.getParameterMap().get(OAuth2ParameterNames.STATE))
                    .findFirst()
                    .orElse(null);
        }
    }
}
