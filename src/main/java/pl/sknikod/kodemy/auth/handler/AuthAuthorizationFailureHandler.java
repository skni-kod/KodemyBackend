package pl.sknikod.kodemy.auth.handler;

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

@Component
@AllArgsConstructor
public class AuthAuthorizationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private AuthCookieAuthorizationRequestRepository authCookieAuthorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String redirect = Cookie.getCookie(request, AuthCookieAuthorizationRequestRepository.REDIRECT_URI_COOKIE_NAME);
        if (redirect == null) redirect = "/";
        String redirectUri = UriComponentsBuilder
                .fromUriString(redirect)
                .queryParam("error", exception.getLocalizedMessage())
                .build()
                .toUriString();

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }

    protected final void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        authCookieAuthorizationRequestRepository.removeAuthorizationSession(request, response);
    }
}