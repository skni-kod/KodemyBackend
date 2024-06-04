package pl.sknikod.kodemygateway.infrastructure.module.oauth2.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.ServerWebExchange;
import pl.sknikod.kodemygateway.configuration.properties.DatabusProperties;
import reactor.core.publisher.Mono;

@Slf4j
public class OAuth2AuthorizationSuccessHandler implements ServerAuthenticationSuccessHandler {
    private final DispatcherHandler dispatcherHandler;
    private final DatabusProperties databusProperties;

    public OAuth2AuthorizationSuccessHandler(
            DispatcherHandler dispatcherHandler,
            DatabusProperties databusProperties
    ) {
        this.dispatcherHandler = dispatcherHandler;
        this.databusProperties = databusProperties;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        addAuthorizationHeader(exchange);
        return dispatcherHandler.handle(exchange);
    }

    private void addAuthorizationHeader(ServerWebExchange exchange) {
        exchange.getResponse().getHeaders()
                .add(HttpHeaders.AUTHORIZATION, databusProperties.getBasicAuthHeader());
    }
}
