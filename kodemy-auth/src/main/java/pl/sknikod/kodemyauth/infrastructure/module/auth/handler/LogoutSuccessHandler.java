package pl.sknikod.kodemyauth.infrastructure.module.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    private final String gatewayRoute;

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            @Null Authentication authentication
    ) throws IOException {
        getRedirectStrategy().sendRedirect(request, response, gatewayRoute);
    }
}