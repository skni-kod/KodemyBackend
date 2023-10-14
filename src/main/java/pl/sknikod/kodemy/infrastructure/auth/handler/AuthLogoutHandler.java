package pl.sknikod.kodemy.infrastructure.auth.handler;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.util.Cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static pl.sknikod.kodemy.infrastructure.auth.rest.AuthController.REDIRECT_URI_PARAMETER;
import static pl.sknikod.kodemy.infrastructure.auth.AuthorizationRequestRepositoryImpl.REDIRECT_URI_COOKIE_NAME;

@Component
@AllArgsConstructor
public class AuthLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String redirectUriAfterLogout = request.getParameter(REDIRECT_URI_PARAMETER);

        if (StringUtils.isNotBlank(redirectUriAfterLogout)) {
            Cookie.addCookie(response, REDIRECT_URI_COOKIE_NAME, redirectUriAfterLogout, 5 * 60);
        }
    }
}
