package pl.sknikod.kodemy.auth.handler;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pl.sknikod.kodemy.auth.AuthSessionRequestRepository;
import pl.sknikod.kodemy.util.Cookie;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class AuthAuthorizationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private AuthSessionRequestRepository authSessionRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String redirect = Cookie.getCookie(request, AuthSessionRequestRepository.REDIRECT_URI_COOKIE);
        if (redirect == null) redirect = "/api/me";

        String redirectUri = UriComponentsBuilder
                .fromUriString(redirect)
                .build()
                .toUriString();

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }
    private final void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authSessionRequestRepository.removeAuthorizationSession(request, response);
    }
}