package pl.sknikod.kodemygateway.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class TokenRelayFilter implements GlobalFilter {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final URI refreshTokenUri;
    private final Integer accessTokenAge;
    private final Integer refreshTokenAge;

    public TokenRelayFilter(WebClient webClient,
                            ObjectMapper objectMapper,
                            @Value("${service.uri.refreshAccessToken}") String refreshTokenUri,
                            @Value("${app.security.cookie.access-token-age}") Integer accessTokenAge,
                            @Value("${app.security.cookie.refresh-token-age}") Integer refreshTokenAge) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.refreshTokenUri = URI.create(refreshTokenUri);
        this.accessTokenAge = accessTokenAge;
        this.refreshTokenAge = refreshTokenAge;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String accessToken = getCookieValue(exchange, AuthCookies.ACCESS_TOKEN);
        String refreshToken = getCookieValue(exchange, AuthCookies.REFRESH_TOKEN);
        if (accessToken == null) {
            return chain.filter(exchange);
        }
        if (isTokenExpired(accessToken)) {
            if (refreshToken == null) {
                return chain.filter(exchange);
            }
            return refreshAccessToken(refreshToken)
                    .flatMap(newAccessTokens -> {
                        setAuthorizationHeader(exchange, newAccessTokens.token());
                        updateAuthCookie(exchange, newAccessTokens);
                        return chain.filter(exchange);
                    })
                    .onErrorMap(th -> new RuntimeException("Refresh token expired", th));
        }
        setAuthorizationHeader(exchange, accessToken);
        return chain.filter(exchange);
    }

    private String getCookieValue(ServerWebExchange exchange, String name) {
        return Optional.ofNullable(exchange.getRequest().getCookies().getFirst(name))
                .map(HttpCookie::getValue)
                .filter(Strings::isNotEmpty)
                .orElse(null);
    }

    public boolean isTokenExpired(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                log.error("Invalid JWT structure");
                return true;
            }

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> claims = objectMapper.readValue(
                    payload, TypeFactory.defaultInstance().constructMapType(Map.class, String.class, Object.class)
            );

            Integer exp = (Integer) claims.get("exp");
            return exp == null || exp * 1000L < System.currentTimeMillis();
        } catch (Exception e) {
            log.error("Error decoding JWT", e);
            return true;
        }
    }

    private Mono<RefreshTokensResponse> refreshAccessToken(String refreshToken) {
        URI uri = UriComponentsBuilder.fromUri(refreshTokenUri)
                .queryParam("grant_type", "refresh_token")
                .queryParam("refresh_token", refreshToken)
                .build().toUri();
        return webClient.post()
                .uri(uri)
                .retrieve()
                .bodyToMono(RefreshTokensResponse.class);
    }

    private void setAuthorizationHeader(ServerWebExchange exchange, String accessToken) {
        exchange.getRequest()
                .mutate()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();
    }

    private void updateAuthCookie(ServerWebExchange exchange, RefreshTokensResponse tokens) {
        var accessTokenCookie = AuthCookies.create(
                AuthCookies.ACCESS_TOKEN, tokens.token(), Duration.ofMinutes(accessTokenAge)
        );
        var refreshTokenCookie = AuthCookies.create(
                AuthCookies.REFRESH_TOKEN, tokens.refresh(), Duration.ofMinutes(refreshTokenAge)
        );
        exchange.getResponse()
                .getHeaders()
                .addAll(HttpHeaders.SET_COOKIE, List.of(accessTokenCookie.toString(), refreshTokenCookie.toString()));
    }

    public record RefreshTokensResponse(String refresh, String token) {
    }
}

