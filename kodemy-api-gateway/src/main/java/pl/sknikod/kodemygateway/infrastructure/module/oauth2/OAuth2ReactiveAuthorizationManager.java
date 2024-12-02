package pl.sknikod.kodemygateway.infrastructure.module.oauth2;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Component
public class OAuth2ReactiveAuthorizationManager implements ReactiveAuthenticationManager {
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.defer(() -> {
            final var token = (OAuth2AuthorizationCodeAuthenticationToken) authentication;
            final var exchange = token.getAuthorizationExchange();
            if (exchange.getAuthorizationResponse().statusError()) {
                return Mono.error(new OAuth2AuthorizationException(exchange.getAuthorizationResponse().getError()));
            }
            if (!isStateEqually(exchange)) {
                return Mono.error(new OAuth2AuthorizationException(new OAuth2Error("invalid_state_parameter")));
            }
            return Mono.just(toOAuth2LoginAuthenticationToken(token)).onErrorMap(OAuth2AuthorizationException.class,
                    (e) -> new OAuth2AuthenticationException(e.getError(), e.getError().toString(), e));
        });
    }

    private boolean isStateEqually(OAuth2AuthorizationExchange exchange) {
        return exchange.getAuthorizationRequest().getState()
                .equals(exchange.getAuthorizationResponse().getState());
    }

    private Authentication toOAuth2LoginAuthenticationToken(OAuth2AuthorizationCodeAuthenticationToken token) {
        return new OAuth2LoginAuthenticationToken(
                token.getClientRegistration(),
                token.getAuthorizationExchange(),
                new DefaultOAuth2User(token.getAuthorities(), Map.of("name", token.getName()), "name"),
                token.getAuthorities(),
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "tokenValue", Instant.now(), Instant.now()),
                token.getRefreshToken()
        );
    }
}
