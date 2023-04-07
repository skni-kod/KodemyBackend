package pl.sknikod.kodemy.auth.handler;

import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository;
import pl.sknikod.kodemy.util.Cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository.REDIRECT_URI_COOKIE_NAME;

@Component
public class AuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private AuthCookieAuthorizationRequestRepository authCookieAuthorizationRequestRepository;

    @Value("${springdoc.swagger-ui.path}")
    private String springdocSwaggerUiPAth;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (response.isCommitted()) return;

        String redirectAfter = Option.of(request)
                .flatMap(req -> Option.of(Cookie.getCookie(req, REDIRECT_URI_COOKIE_NAME))
                        .orElse(Option.of(req.getHeader("Referer")))
                )
                .getOrElse(springdocSwaggerUiPAth);

        String redirectAfterUri = UriComponentsBuilder
                .fromUriString(redirectAfter)
                .build()
                .toUriString();

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, redirectAfterUri);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authCookieAuthorizationRequestRepository.removeAuthorizationSession(request, response);
    }
}