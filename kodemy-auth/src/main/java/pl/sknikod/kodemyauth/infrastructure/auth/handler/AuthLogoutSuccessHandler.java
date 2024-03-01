package pl.sknikod.kodemyauth.infrastructure.auth.handler;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static pl.sknikod.kodemyauth.infrastructure.auth.rest.AuthControllerDefinition.REDIRECT_URI_PARAMETER;

@Component
@RequiredArgsConstructor
public class AuthLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (response.isCommitted()) return;

        var url = Option.of(request.getParameter(REDIRECT_URI_PARAMETER))
                .getOrElse(request.getHeader(HttpHeaders.REFERER));
        String redirectUri = Option.of(url)
                .map(uri -> UriComponentsBuilder.fromUriString(uri).build().toString())
                .getOrElse("");

        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }
}