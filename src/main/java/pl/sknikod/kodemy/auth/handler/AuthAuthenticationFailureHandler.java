package pl.sknikod.kodemy.auth.handler;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository;
import pl.sknikod.kodemy.util.Cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository.REDIRECT_URI_COOKIE_NAME;

@Component
@AllArgsConstructor
public class AuthAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private AuthCookieAuthorizationRequestRepository authCookieAuthorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String redirectAfter = Option.of(request)
                .flatMap(req -> Option.of(Cookie.getCookie(req, REDIRECT_URI_COOKIE_NAME))
                        .orElse(Option.of(req.getHeader("Referer")))
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
        authCookieAuthorizationRequestRepository.removeAuthorizationSession(request, response);
    }
}