package pl.sknikod.kodemygateway.util.filter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class GatewayAuthFilter extends AbstractGatewayFilterFactory<Object> {
    @Override
    public GatewayFilter apply(Object config) {
        return ((exchange, chain) -> chain.filter(exchange));
    }

    @Configuration
    @Data
    @ConfigurationProperties(prefix = "kodemy.gateway")
    public static class GatewayProperties {
        private List<String> openEndpoints;

        public Predicate<ServerHttpRequest> isSecured() {
            return request -> openEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
        }
    }
}
