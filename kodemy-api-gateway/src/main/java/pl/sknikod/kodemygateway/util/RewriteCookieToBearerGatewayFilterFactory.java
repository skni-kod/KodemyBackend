package pl.sknikod.kodemygateway.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@Slf4j
public class RewriteCookieToBearerGatewayFilterFactory
        extends AbstractGatewayFilterFactory<Object> {

    public RewriteCookieToBearerGatewayFilterFactory() {
        super(Object.class);
    }

    @Override
    public GatewayFilter apply(Object config) {
        return new RewriteCookieToBearerFilter();
    }

    @RequiredArgsConstructor
    private static final class RewriteCookieToBearerFilter implements GatewayFilter {
        private static final String ACCESS_TOKEN_COOKIE = "AUTH_CONTEXT";

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            return Optional.ofNullable(exchange.getRequest().getCookies().getFirst(ACCESS_TOKEN_COOKIE))
                    .map(HttpCookie::getValue)
                    .filter(Strings::isNotEmpty)
                    .map(token -> {
                        log.info("Rewrite cookie to {} header", HttpHeaders.AUTHORIZATION);
                        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                                .header(HttpHeaders.COOKIE, (String) null)
                                .build();
                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    })
                    .orElseGet(() -> chain.filter(exchange));
        }
    }
}
