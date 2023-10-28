package pl.sknikod.kodemygateway.util.filter;

import io.vavr.control.Try;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.sknikod.kodemygateway.infrastructure.AuthResponse;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Component
@DependsOn("appConfig")
public class AuthFilter extends AbstractGatewayFilterFactory<Object> {
    private static final String BEARER_PREFIX = "Bearer ";
    @Value("${kodemy.service.auth-get-token}")
    private String authGetTokenUrl;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public GatewayFilter apply(Object config) {
        return ((exchange, chain) -> {
            var reqMutate = exchange.getRequest().mutate();

            String bearer = Try.of(() -> restTemplate.getForObject(authGetTokenUrl, AuthResponse.class))
                    .map(AuthResponse::getBearer)
                    .getOrNull();

            if (Objects.isNull(bearer)) {
                reqMutate.headers(httpHeaders -> httpHeaders.remove(HttpHeaders.AUTHORIZATION));
            } else {
                reqMutate.header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + bearer);
            }
            return chain.filter(exchange);
        });
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
