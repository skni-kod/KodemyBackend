package pl.sknikod.kodemy.auth.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository;
import pl.sknikod.kodemy.util.Cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        //super.onLogoutSuccess(request, response, authentication);
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request,response, request.getContextPath());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        Cookie.deleteCookie(request, response,
                AuthCookieAuthorizationRequestRepository.AUTHORIZATION_REQUEST_COOKIE_NAME
        );
        request.getSession().invalidate();
    }
}