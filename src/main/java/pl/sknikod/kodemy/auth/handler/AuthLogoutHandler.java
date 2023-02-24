package pl.sknikod.kodemy.auth.handler;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.util.Cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository.REDIRECT_URI_COOKIE_NAME;

@Component
@AllArgsConstructor
public class AuthLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String redirectUriAfterLogout = request.getParameter("redirect_uri");

        if (StringUtils.isNotBlank(redirectUriAfterLogout)) {
            Cookie.addCookie(response, REDIRECT_URI_COOKIE_NAME, redirectUriAfterLogout, 5 * 60);
        }
    }
}
