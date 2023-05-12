package pl.sknikod.kodemy.auth.handler;

import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.util.Cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static pl.sknikod.kodemy.auth.AuthorizationRequestRepositoryImpl.AUTHORIZATION_REQUEST_COOKIE_NAME;
import static pl.sknikod.kodemy.auth.AuthorizationRequestRepositoryImpl.REDIRECT_URI_COOKIE_NAME;

@Component
public class AuthLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Value("${springdoc.swagger-ui.path:#{T(org.springdoc.core.Constants).DEFAULT_SWAGGER_UI_PATH}}")
    private String springdocSwaggerUiPAth;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (response.isCommitted()) return;

        String redirectUriAfterLogout = Option.of(request)
                .flatMap(req -> Option.of(Cookie.getCookie(req, REDIRECT_URI_COOKIE_NAME))
                        .orElse(Option.of(req.getHeader("Referer")))
                )
                .getOrElse(springdocSwaggerUiPAth);

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, redirectUriAfterLogout);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        Cookie.deleteCookie(request, response, AUTHORIZATION_REQUEST_COOKIE_NAME);
        Cookie.deleteCookie(request, response, REDIRECT_URI_COOKIE_NAME);
        request.getSession().invalidate();
    }
}