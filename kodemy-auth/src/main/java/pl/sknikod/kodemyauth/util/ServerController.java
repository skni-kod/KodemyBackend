package pl.sknikod.kodemyauth.util;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import pl.sknikod.kodemyauth.exception.ExceptionRestGenericMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

@Controller
public class ServerController {
    protected static final JsonNodeFactory JSON_NODE_FACTORY = JsonNodeFactory.instance;

    public void setJsonResponseHeaders(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
    }

    @SneakyThrows
    public void unauthorizedResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException ex
    ) {
        setJsonResponseHeaders(response);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        var rootNode = new ExceptionRestGenericMessage(HttpStatus.UNAUTHORIZED, ex).toObjectNode();
        response.getWriter().write(rootNode.toPrettyString());
    }

    @SneakyThrows
    public void forbiddenResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException ex
    ) {
        setJsonResponseHeaders(response);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        var rootNode = new ExceptionRestGenericMessage(HttpStatus.UNAUTHORIZED, ex).toObjectNode();
        response.getWriter().write(rootNode.toPrettyString());
    }
}

