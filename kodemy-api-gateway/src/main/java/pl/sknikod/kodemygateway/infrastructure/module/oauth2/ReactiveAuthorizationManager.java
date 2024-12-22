package pl.sknikod.kodemygateway.infrastructure.module.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemygateway.infrastructure.module.oauth2.model.AuthorizeResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ReactiveAuthorizationManager implements ReactiveAuthenticationManager {
    private final AuthorizeResponseClient authorizeResponseClient;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.defer(() -> {
            final var token = (OAuth2AuthorizationCodeAuthenticationToken) authentication;
            final var authorizationResponse = token.getAuthorizationExchange().getAuthorizationResponse();
            if (authorizationResponse.statusError()) {
                return Mono.error(new OAuth2AuthorizationException(authorizationResponse.getError()));
            }
            final var authorizationRequest = token.getAuthorizationExchange().getAuthorizationRequest();
            if (!authorizationResponse.getState().equals(authorizationRequest.getState())) {
                return Mono.error(new OAuth2AuthorizationException(new OAuth2Error("invalid_state_parameter")));
            }
            return this.authorizeResponseClient.getAuthorizeResponse(token).map(toToken(token))
                    .onErrorMap(OAuth2AuthorizationException.class,
                            (e) -> new OAuth2AuthenticationException(e.getError(), e.getError().toString(), e));
        });
    }

    private Function<? super AuthorizeResponse, Authentication> toToken(OAuth2AuthorizationCodeAuthenticationToken token) {
        return authorizeResponse -> {
            Instant instant = Instant.now();
            Instant instantPlus = Instant.from(instant).plus(Duration.ofMinutes(10));
            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER, authorizeResponse.getAccessToken(), instant, instantPlus);
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(authorizeResponse.getRefreshToken(), instant, instantPlus);
            Map<String, Object> attributes = Map.of(
                    "name", token.getName(), "accessToken", accessToken, "refreshToken", refreshToken);
            return new OAuth2LoginAuthenticationToken(
                    token.getClientRegistration(),
                    token.getAuthorizationExchange(),
                    new DefaultOAuth2User(token.getAuthorities(), attributes, "name"),
                    token.getAuthorities(),
                    accessToken, refreshToken
            );
        };
    }
}
