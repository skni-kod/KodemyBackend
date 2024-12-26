package pl.sknikod.kodemygateway.infrastructure.module.oauth2.handler;

import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;

public abstract class AuthenticationHandler {
    protected static final String ACCESS_TOKEN_COOKIE = "AUTH_CONTEXT";
    protected static final String REFRESH_TOKEN_COOKIE = "AUTH_PERSIST";
    private final String frontBaseUrl;

    public AuthenticationHandler(String frontBaseUrl) {
        this.frontBaseUrl = frontBaseUrl;
    }

    private void performRedirect(ServerHttpResponse response, URI uri) {
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(uri);
    }

    protected void performRedirect(ServerHttpResponse response) {
        URI uri = UriComponentsBuilder.fromUriString(frontBaseUrl)
                .queryParam("auth", "success")
                .build().toUri();
        performRedirect(response, uri);
    }

    protected void performRedirect(ServerHttpResponse response, AuthenticationException exception) {
        URI uri = UriComponentsBuilder.fromUriString(frontBaseUrl)
                .queryParam("auth", "failure")
                .queryParams(createParams(exception))
                .build().toUri();
        performRedirect(response, uri);
    }

    private MultiValueMap<String, String> createParams(AuthenticationException exception) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (exception instanceof OAuth2AuthenticationException oauth2Exception) {
            OAuth2Error error = oauth2Exception.getError();
            if (error != null) {
                params.add("error", error.getErrorCode());
                return params;
            }
        }
        params.add("error", OAuth2ErrorCodes.SERVER_ERROR);
        return params;
    }
}
