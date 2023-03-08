package pl.sknikod.kodemy.auth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.exception.UnauthorizedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getLocalizedMessage());
        throw new UnauthorizedException(authException.getMessage());
    }
}