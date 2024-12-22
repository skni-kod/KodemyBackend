package pl.sknikod.kodemygateway.infrastructure.module.oauth2.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemygateway.util.AuthCookies;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class AuthenticationSuccessHandler extends AuthenticationHandler implements ServerAuthenticationSuccessHandler {
    private final Integer accessTokenAge;
    private final Integer refreshTokenAge;

    public AuthenticationSuccessHandler(@Value("${service.baseUrl.front}") String frontBaseUrl,
                                        @Value("${app.security.cookie.access-token-age}") Integer accessTokenAge,
                                        @Value("${app.security.cookie.refresh-token-age}") Integer refreshTokenAge) {
        super(frontBaseUrl);
        this.accessTokenAge = accessTokenAge;
        this.refreshTokenAge = refreshTokenAge;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        modifyHeaders(response, (OAuth2AuthenticationToken) authentication);
        performRedirect(response);
        return Mono.empty();
    }

    private void modifyHeaders(ServerHttpResponse response, OAuth2AuthenticationToken token) {
        HttpHeaders headers = response.getHeaders();

        Map<String, Object> attributes = token.getPrincipal().getAttributes();

        OAuth2AccessToken accessToken = (OAuth2AccessToken) attributes.get("accessToken");
        OAuth2RefreshToken refreshToken = (OAuth2RefreshToken) attributes.get("refreshToken");
        var accessTokenCookie = AuthCookies.create(
                AuthCookies.ACCESS_TOKEN, accessToken.getTokenValue(), Duration.ofMinutes(accessTokenAge)
        );
        var refreshTokenCookie = AuthCookies.create(
                AuthCookies.REFRESH_TOKEN, refreshToken.getTokenValue(), Duration.ofMinutes(refreshTokenAge)
        );
        headers.addAll(HttpHeaders.SET_COOKIE, List.of(accessTokenCookie.toString(), refreshTokenCookie.toString()));
    }
}
