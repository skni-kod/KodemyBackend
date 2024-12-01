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

import java.util.List;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.CLIENT_RESPONSE_CONN_ATTR;

@Component
@Slf4j
public class LogoutGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    public LogoutGatewayFilterFactory() {
        super(Object.class);
    }

    @Override
    public GatewayFilter apply(Object config) {
        return new LogoutGatewayFilter();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Config {
        private String location;
    }

    @RequiredArgsConstructor
    private static final class LogoutGatewayFilter implements GatewayFilter, Ordered {
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
                        }
                        return Mono.empty();
                    }));
        }

        private void modifyHeaders(ServerHttpResponse response) {
            if (response.getStatusCode() != HttpStatus.OK) {
                log.warn("Response status code is {}. Execute modifyHeaders anyway", response.getStatusCode());
            }
            response.getHeaders().addAll(
                    HttpHeaders.SET_COOKIE,
                    List.of(createExpiredCookie(ACCESS_TOKEN_COOKIE).toString(), createExpiredCookie(REFRESH_TOKEN_COOKIE).toString())
            );
        }

        private ResponseCookie createExpiredCookie(String name) {
            return ResponseCookie.from(name).maxAge(0).build();
        }
    }
}
