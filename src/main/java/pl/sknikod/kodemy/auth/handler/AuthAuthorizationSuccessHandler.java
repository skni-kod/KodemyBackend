package pl.sknikod.kodemy.auth.handler;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository;
import pl.sknikod.kodemy.util.Cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class AuthAuthorizationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private AuthCookieAuthorizationRequestRepository authCookieAuthorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (response.isCommitted()) return;

        String redirectUriAfterLogin = Cookie.getCookie(request, AuthCookieAuthorizationRequestRepository.REDIRECT_URI_COOKIE_NAME);
        if (redirectUriAfterLogin == null) redirectUriAfterLogin = request.getHeader(HttpHeaders.REFERER);

        String redirectUri = UriComponentsBuilder
                .fromUriString(redirectUriAfterLogin)
                .build()
                .toUriString();

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authCookieAuthorizationRequestRepository.removeAuthorizationSession(request, response);
    }
}