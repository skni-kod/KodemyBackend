package pl.sknikod.kodemyauth.infrastructure.auth.handler;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pl.sknikod.kodemyauth.infrastructure.auth.AuthorizationRequestRepositoryImpl;
import pl.sknikod.kodemyauth.util.Cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static pl.sknikod.kodemyauth.infrastructure.auth.AuthorizationRequestRepositoryImpl.REDIRECT_URI_COOKIE_NAME;

@Component
@RequiredArgsConstructor
public class AuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final AuthorizationRequestRepositoryImpl authorizationRequestRepository;

    @Value("${springdoc.swagger-ui.path:#{T(org.springdoc.core.Constants).DEFAULT_SWAGGER_UI_PATH}}")
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
        authorizationRequestRepository.removeAuthorizationSession(request, response);
    }
}