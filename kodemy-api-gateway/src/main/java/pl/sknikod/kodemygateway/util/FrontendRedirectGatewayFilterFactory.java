package pl.sknikod.kodemygateway.util;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.DisposableChannel;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.CLIENT_RESPONSE_CONN_ATTR;

@Component
@Slf4j
public class FrontendRedirectGatewayFilterFactory
        extends AbstractGatewayFilterFactory<FrontendRedirectGatewayFilterFactory.Config> {

    public FrontendRedirectGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new FrontendRedirectGatewayFilter(config);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Config {
        private String location;
    }

    @RequiredArgsConstructor
    private static final class FrontendRedirectGatewayFilter implements GatewayFilter, Ordered {
        private final Config config;
        private static final String ACCESS_TOKEN_COOKIE = "AUTH_CONTEXT";
        private static final String REFRESH_TOKEN_COOKIE = "AUTH_PERSIST";

        @Override
        public int getOrder() {
            return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            return chain.filter(exchange)
                    .then(Mono.<Void>defer(() -> {
                        if (exchange.getAttribute(CLIENT_RESPONSE_CONN_ATTR) != null) {
                            modifyHeaders(exchange.getResponse());
                            performRedirect(exchange.getResponse());
                            disposeConnection(exchange);
                        }
                        return Mono.empty();
                    }))
                    .doOnCancel(() -> disposeConnection(exchange))
                    .doOnError(th -> disposeConnection(exchange));
        }

        private void modifyHeaders(ServerHttpResponse response) {
            if (response.getStatusCode() != HttpStatus.OK) {
                log.warn("Response status code is {}. Skipping modifyHeaders", response.getStatusCode());
                return;
            }

            HttpHeaders headers = response.getHeaders();
            List<String> accessTokens = headers.get(ACCESS_TOKEN_COOKIE);
            List<String> refreshTokens = headers.get(REFRESH_TOKEN_COOKIE);

            if (accessTokens == null || accessTokens.isEmpty()
                    || refreshTokens == null || refreshTokens.isEmpty()) {
                log.warn("Authorization tokens don't exist or are empty. Skipping modifyHeaders");
                return;
            }

            var accessToken = createCookie(
                    ACCESS_TOKEN_COOKIE,
                    accessTokens.get(0),
                    Duration.ofDays(1)
            );

            var refreshToken = createCookie(
                    REFRESH_TOKEN_COOKIE,
                    refreshTokens.get(0),
                    Duration.ofDays(1)
            );

            headers.addAll(HttpHeaders.SET_COOKIE, List.of(accessToken.toString(), refreshToken.toString()));
        }

        private ResponseCookie createCookie(@NonNull String name, @NonNull String value, @NonNull Duration age) {
            return ResponseCookie.from(name, value)
                    .path("/")
                    .httpOnly(true)
                    .sameSite("Lax")
                    .maxAge(age)
                    .build();
        }

        private void performRedirect(ServerHttpResponse response) {
            response.setStatusCode(HttpStatus.FOUND);
            response.getHeaders().setLocation(URI.create(config.location));
        }

        private void disposeConnection(ServerWebExchange exchange) {
            Optional.ofNullable((Connection) exchange.getAttribute(CLIENT_RESPONSE_CONN_ATTR))
                    .filter(conn -> conn.channel().isActive())
                    .ifPresent(DisposableChannel::dispose);
        }
    }
}
