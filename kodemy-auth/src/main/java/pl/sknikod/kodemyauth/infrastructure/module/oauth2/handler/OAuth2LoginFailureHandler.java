package pl.sknikod.kodemyauth.infrastructure.module.oauth2.handler;

import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import pl.sknikod.kodemyauth.util.route.RouteRedirectStrategy;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final String redirectPath;
    private static final Map<String, String> GENERAL_ERROR_PARAMS = Map.of("error", "authentication_error");

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException exception
    ) {
        final var params = postProcess(exception)
                .fold(th -> GENERAL_ERROR_PARAMS, obj -> GENERAL_ERROR_PARAMS);
        ((RouteRedirectStrategy) getRedirectStrategy()).sendRedirect(request, response, redirectPath, params);
    }

    private Try<Object> postProcess(AuthenticationException exception) {
        return Try.failure(exception)
                .onFailure(th -> log.error("Authentication failure: ", th));
    }
}