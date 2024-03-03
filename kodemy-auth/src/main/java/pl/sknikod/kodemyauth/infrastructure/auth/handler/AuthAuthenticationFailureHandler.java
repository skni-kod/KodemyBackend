package pl.sknikod.kodemyauth.infrastructure.auth.handler;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pl.sknikod.kodemyauth.configuration.SecurityConfig;
import pl.sknikod.kodemyauth.util.Base64Util;
import pl.sknikod.kodemyauth.util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final SecurityConfig.SecurityProperties.AuthProperties authProperties;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        String redirectAfter = Option.of(request)
                .flatMap(req -> Option.of(CookieUtil.getCookie(req, authProperties.getKey().getRedirect()))
                        .map(v -> (String) Base64Util.decode(v))
                        .orElse(Option.of(req.getHeader(HttpHeaders.REFERER)))
                )
                .getOrElse("/");

        String redirectAfterUri = UriComponentsBuilder
                .fromUriString(redirectAfter)
                .queryParam("error", exception.getLocalizedMessage())
                .build()
                .toUriString();

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, redirectAfterUri);
    }

    protected final void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        Option.of(request.getSession()).peek(session -> session.removeAttribute(authProperties.getKey().getCurrentSession()));
        CookieUtil.deleteCookie(request, response, authProperties.getKey().getRedirect());
    }
}