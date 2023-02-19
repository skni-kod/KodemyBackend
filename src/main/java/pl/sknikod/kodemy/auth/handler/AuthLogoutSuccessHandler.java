package pl.sknikod.kodemy.auth.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.util.Cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static pl.sknikod.kodemy.auth.AuthController.DEFAULT_REDIRECT_URL_AFTER_LOGOUT;
import static pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository.AUTHORIZATION_REQUEST_COOKIE_NAME;
import static pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository.REDIRECT_URI_COOKIE_NAME;

@Component
public class AuthLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        //super.onLogoutSuccess(request, response, authentication);
        if (response.isCommitted()) return;

        String redirectUriAfterLogout = Cookie.getCookie(request, REDIRECT_URI_COOKIE_NAME);
        if (redirectUriAfterLogout == null) redirectUriAfterLogout = DEFAULT_REDIRECT_URL_AFTER_LOGOUT;

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, redirectUriAfterLogout);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        Cookie.deleteCookie(request, response, AUTHORIZATION_REQUEST_COOKIE_NAME);
        Cookie.deleteCookie(request, response, REDIRECT_URI_COOKIE_NAME);
        request.getSession().invalidate();
    }
}