package pl.sknikod.kodemygateway.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.DisposableChannel;

import java.time.Duration;
import java.util.Optional;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.CLIENT_RESPONSE_CONN_ATTR;

@Component
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

    private record FrontendRedirectGatewayFilter(Config config) implements GatewayFilter, Ordered {
        @Override
        public int getOrder() {
            return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            return chain.filter(exchange)
                    .then(Mono.<Void>defer(() -> {
                        if (exchange.getAttribute(CLIENT_RESPONSE_CONN_ATTR) != null) {
                            ServerHttpResponse response = exchange.getResponse();
                            addCookieToResponse(response);
                            performRedirect(response);
                            disposeConnection(exchange);
                        }
                        return Mono.empty();
                    }))
                    .doOnCancel(() -> disposeConnection(exchange))
                    .doOnError(th -> disposeConnection(exchange));
        }

        private void addCookieToResponse(ServerHttpResponse response) {
            response.addCookie(ResponseCookie.from("  ", "test")
                    .path("/")
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(Duration.ofDays(1))
                    .build());
        }

        private void performRedirect(ServerHttpResponse response) {
            response.setStatusCode(HttpStatus.FOUND);
            response.getHeaders().setLocation(java.net.URI.create(config.location));
        }

        private void disposeConnection(ServerWebExchange exchange) {
            Optional.ofNullable((Connection) exchange.getAttribute(CLIENT_RESPONSE_CONN_ATTR))
                    .filter(conn -> conn.channel().isActive())
                    .ifPresent(DisposableChannel::dispose);
        }
    }
}
