package pl.sknikod.kodemyauth.infrastructure.module.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        int x = 0;
        /*if (response.isCommitted()) return;

        var url = Option.of(request.getParameter(REDIRECT_URI_PARAMETER))
                .getOrElse(request.getHeader(HttpHeaders.REFERER));
        String redirectUri = Option.of(url)
                .map(uri -> UriComponentsBuilder.fromUriString(uri).build().toString())
                .getOrElse("");

        getRedirectStrategy().sendRedirect(request, response, redirectUri);*/
    }
}