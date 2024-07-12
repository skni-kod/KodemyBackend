package pl.sknikod.kodemysearch.util.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import pl.sknikod.kodemysearch.exception.ExceptionBody;

import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class AccessControlExceptionHandler {
    private final ObjectMapper objectMapper;

    public void entryPoint(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) {
        writeError(response, ex);
    }

    public void accessDenied(
            HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) {
        writeError(response, ex);
    }

    private void writeError(HttpServletResponse response, RuntimeException ex) {
        final var httpStatus = determineHttpStatus(ex);
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        ExceptionBody body = new ExceptionBody(httpStatus, ex);
        Try.run(() -> response.getWriter().write(objectMapper.writeValueAsString(body)))
                .onFailure(th -> log.error("Error writing to response: ", th));
    }

    private HttpStatus determineHttpStatus(RuntimeException ex) {
        if (ex instanceof AccessDeniedException)
            return HttpStatus.FORBIDDEN;
        return HttpStatus.UNAUTHORIZED;
    }
}
