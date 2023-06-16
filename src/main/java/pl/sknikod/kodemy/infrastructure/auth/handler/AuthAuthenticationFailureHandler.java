package pl.sknikod.kodemy.infrastructure.auth.handler;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pl.sknikod.kodemy.infrastructure.auth.AuthorizationRequestRepositoryImpl;
import pl.sknikod.kodemy.util.Cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static pl.sknikod.kodemy.infrastructure.auth.AuthorizationRequestRepositoryImpl.REDIRECT_URI_COOKIE_NAME;

@Component
@RequiredArgsConstructor
public class AuthAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final AuthorizationRequestRepositoryImpl authorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String redirectAfter = Option.of(request)
                .flatMap(req -> Option.of(Cookie.getCookie(req, REDIRECT_URI_COOKIE_NAME))
                        .orElse(Option.of(req.getHeader("Referer")))
                )
                .getOrElse("/");

        String redirectAfterUri = UriComponentsBuilder
                .fromUriString(redirectAfter)
                .queryParam("error", exception.getLocalizedMessage())
                .build()
                .toUriString();

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, redirectAfterUri);
    }

    protected final void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        authorizationRequestRepository.removeAuthorizationSession(request, response);
    }
}