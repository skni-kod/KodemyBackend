package pl.sknikod.kodemy.auth.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository;
import pl.sknikod.kodemy.util.Cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static pl.sknikod.kodemy.auth.AuthController.DEFAULT_REDIRECT_URL_AFTER_LOGIN;
import static pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository.REDIRECT_URI_COOKIE_NAME;

@Component
public class AuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private AuthCookieAuthorizationRequestRepository authCookieAuthorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (response.isCommitted()) return;

        String redirectUriAfterLogin = Cookie.getCookie(request, REDIRECT_URI_COOKIE_NAME);
        if (redirectUriAfterLogin == null) redirectUriAfterLogin = DEFAULT_REDIRECT_URL_AFTER_LOGIN;

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, redirectUriAfterLogin);
    }
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authCookieAuthorizationRequestRepository.removeAuthorizationSession(request, response);
    }
}