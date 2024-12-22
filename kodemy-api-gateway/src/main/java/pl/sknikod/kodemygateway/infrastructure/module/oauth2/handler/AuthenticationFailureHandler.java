package pl.sknikod.kodemygateway.infrastructure.module.oauth2.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFailureHandler extends AuthenticationHandler implements ServerAuthenticationFailureHandler {
    public AuthenticationFailureHandler(@Value("${service.baseUrl.front}") String frontBaseUrl) {
        super(frontBaseUrl);
    }

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        performRedirect(response, exception);
        return Mono.empty();
    }
}
