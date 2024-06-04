package pl.sknikod.kodemygateway.infrastructure.module.oauth2.util.route;

import lombok.RequiredArgsConstructor;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GatewayRedirectStrategy extends DefaultServerRedirectStrategy {
    public Mono<Void> sendRedirect(
            String location, ServerWebExchange exchange, MultiValueMap<String, String> params) {
        return super.sendRedirect(exchange, UriBuilder.location(location).params(params).build());
    }

    public Mono<Void> sendRedirect(
            String location, ServerWebExchange exchange, Map<String, String> params) {
        return super.sendRedirect(exchange, UriBuilder.location(location).params(params).build());
    }

    private record UriBuilder(UriComponentsBuilder locationBuilder) {
        public static UriBuilder location(String location) {
            return new UriBuilder(convert(location));
        }

        private static UriComponentsBuilder convert(String location) {
            return UriComponentsBuilder.fromUriString(location.length() <= 2 ? location.replace("//", "/") : location);
        }

        public UriBuilder params(MultiValueMap<String, String> params) {
            params.forEach((key, values) -> values.forEach(value -> locationBuilder.queryParam(key, value)));
            return this;
        }

        public UriBuilder params(Map<String, String> params) {
            params.forEach(locationBuilder::queryParam);
            return this;
        }

        public URI build() {
            return locationBuilder.build().toUri();
        }
    }
}
