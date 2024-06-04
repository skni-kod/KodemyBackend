package pl.sknikod.kodemygateway.infrastructure.module.oauth2.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.web.server.ServerWebExchange;
import pl.sknikod.kodemygateway.infrastructure.module.oauth2.util.route.GatewayRedirectStrategy;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Slf4j
public class OAuth2AuthorizationFailureHandler implements ServerAuthenticationFailureHandler {
    private final GatewayRedirectStrategy gatewayRedirectStrategy;
    private final String failureLocation;

    public OAuth2AuthorizationFailureHandler(
            GatewayRedirectStrategy gatewayRedirectStrategy,
            String frontRouteBaseUrl,
            String errorPath
    ) {
        this.failureLocation = frontRouteBaseUrl + errorPath;
        this.gatewayRedirectStrategy = gatewayRedirectStrategy;
    }

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        log.warn("Authentication failed: {}", exception.getMessage());
        ServerWebExchange exchange = webFilterExchange.getExchange();
        return this.gatewayRedirectStrategy.sendRedirect(failureLocation, exchange, Collections.emptyMap());
    }
}
