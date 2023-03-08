package pl.sknikod.kodemy.auth.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.exception.ForbiddenException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, accessDeniedException.getLocalizedMessage());
        throw new ForbiddenException(accessDeniedException.getMessage());
    }
}