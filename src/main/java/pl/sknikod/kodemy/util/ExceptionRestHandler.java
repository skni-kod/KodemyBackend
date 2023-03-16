package pl.sknikod.kodemy.util;

import lombok.NonNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.sknikod.kodemy.exception.GeneralException;
import pl.sknikod.kodemy.exception.OAuth2AuthenticationProcessingException;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public final class ExceptionRestHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            InsufficientAuthenticationException.class,
            OAuth2AuthenticationProcessingException.class
    })
    public ResponseEntity<Object> handleAuthException(Exception exception) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (exception instanceof InsufficientAuthenticationException) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (exception instanceof OAuth2AuthenticationProcessingException) {
            status = HttpStatus.BAD_REQUEST;
        }

        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage(status, exception.getMessage());
        return ResponseEntity.status(status).body(excMessage);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Object> handleGeneralException(GeneralException exception) {
        var exceptionRestGenericMessage = exception.getExceptionRestGenericMessage();
        return ResponseEntity.status(exceptionRestGenericMessage.getStatus()).body(exceptionRestGenericMessage);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exc, Object body, @NonNull HttpHeaders headers, @NotNull HttpStatus status, WebRequest request
    ) {
        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage(
                LocalDateTime.now(),
                status.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                exc.getMessage()
        );
        return new ResponseEntity<>(excMessage, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exc, @NonNull HttpHeaders headers, HttpStatus status, @NonNull WebRequest request
    ) {
        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage(
                LocalDateTime.now(),
                status.value(),
                "Unsupported Media Type",
                exc.getMessage()
        );
        return new ResponseEntity<>(excMessage, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exc, @NonNull HttpHeaders headers, HttpStatus status, @NonNull WebRequest request
    ) {
        BindingResult result = exc.getBindingResult();
        List<String> errorList = result.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage(
                LocalDateTime.now(),
                status.value(),
                "Method Argument Not Valid",
                errorList.toString()
        );
        return new ResponseEntity<>(excMessage, status);
    }
}