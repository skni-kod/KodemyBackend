package pl.sknikod.kodemygateway.infrastructure.module.oauth2;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemygateway.infrastructure.module.oauth2.model.GatewayOAuth2User;
import pl.sknikod.kodemygateway.infrastructure.module.oauth2.util.OAuth2Constant;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Component
public class OAuth2ReactiveAuthorizationManager implements ReactiveAuthenticationManager {
    private static final OAuth2Error DIFFERENT_STATE_ERROR = new OAuth2Error(OAuth2Constant.STATE_DIFFERENT);
    private static final Set<GrantedAuthority> EMPTY_AUTHORITIES = Collections.emptySet();
    private static final OAuth2AccessToken DEFAULT_ACCESS_TOKEN = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER, "gateway_token_value", null, null
    );
    private static final Map<String, Object> EMPTY_ATTRIBUTES = Collections.emptyMap();

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.defer(() -> {
            final var token = (OAuth2AuthorizationCodeAuthenticationToken) authentication;
            final var exchange = token.getAuthorizationExchange();

            final OAuth2AuthorizationResponse authorizationResponse = exchange.getAuthorizationResponse();
            if (authorizationResponse.statusError())
                return Mono.error(new OAuth2AuthorizationException(authorizationResponse.getError()));

            final OAuth2AuthorizationRequest authorizationRequest = exchange.getAuthorizationRequest();
            if (!authorizationResponse.getState().equals(authorizationRequest.getState()))
                return Mono.error(new OAuth2AuthorizationException(DIFFERENT_STATE_ERROR));

            return Mono.just(getOAuth2LoginAuthenticationToken(token));
        });
    }

    private Authentication getOAuth2LoginAuthenticationToken(OAuth2AuthorizationCodeAuthenticationToken token) {
        return new OAuth2LoginAuthenticationToken(
                token.getClientRegistration(),
                token.getAuthorizationExchange(),
                new GatewayOAuth2User(EMPTY_AUTHORITIES, EMPTY_ATTRIBUTES),
                EMPTY_AUTHORITIES,
                DEFAULT_ACCESS_TOKEN,
                token.getRefreshToken()
        );
    }
}
