package pl.sknikod.kodemyauth.infrastructure.module.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    private final String gatewayRoute;

    public LogoutSuccessHandler(@Value("${network.route.gateway}") String gatewayRoute) {
        this.gatewayRoute = gatewayRoute;
    }

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            @Null Authentication authentication
    ) throws IOException {
        getRedirectStrategy().sendRedirect(request, response, gatewayRoute);
    }
}