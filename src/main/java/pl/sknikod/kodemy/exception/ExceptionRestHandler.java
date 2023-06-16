package pl.sknikod.kodemy.exception;

import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;

import javax.validation.constraints.NotNull;

@RestControllerAdvice
public final class ExceptionRestHandler extends ResponseEntityExceptionHandler {
    @Override
    @NonNull
    public @NotNull ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception ex, Object body, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request
    ) {
        var restGenericMessage = body == null ? new ExceptionRestGenericMessage(status, ex) : body;
        return super.handleExceptionInternal(ex, restGenericMessage, headers, status, request);
    }

    @ExceptionHandler({
            InsufficientAuthenticationException.class,
            AuthenticationException.class,
    })
    public ResponseEntity<Object> handleAuthException(Exception ex, WebRequest request) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status;

        if (ex instanceof InsufficientAuthenticationException) status = HttpStatus.UNAUTHORIZED;
        else if (ex instanceof AccessDeniedException) status = HttpStatus.FORBIDDEN;
        else throw ex;

        return this.handleExceptionInternal(ex, null, headers, status, request);
    }

    @ExceptionHandler(ServerProcessingException.class)
    public ResponseEntity<Object> handleGeneralException(ServerProcessingException ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        return this.handleExceptionInternal(ex, null, headers, ex.getHttpStatus(), request);
    }

    @Override
    @NonNull
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request
    ) {
        var errorList = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList()
                .toString();
        var restGenericMessage = new ExceptionRestGenericMessage(status, errorList);
        return this.handleExceptionInternal(ex, restGenericMessage, new HttpHeaders(), status, request);
    }

}