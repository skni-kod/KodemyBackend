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
import java.util.List;

@RestControllerAdvice
public final class ExceptionRestHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            InsufficientAuthenticationException.class,
            OAuth2AuthenticationProcessingException.class
    })
    public ResponseEntity<Object> handleAuthException(Exception exc) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (exc instanceof InsufficientAuthenticationException) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (exc instanceof OAuth2AuthenticationProcessingException) {
            status = HttpStatus.BAD_REQUEST;
        }

        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage(status, exc.getMessage());
        return ResponseEntity.status(status).body(excMessage);
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Object> handleGeneralException(GeneralException exc) {
        var exceptionRestGenericMessage = exc.getExceptionRestGenericMessage();
        return ResponseEntity.status(exceptionRestGenericMessage.getStatus()).body(exceptionRestGenericMessage);
    }

    @Override
    @NonNull
    protected @NotNull ResponseEntity<Object> handleExceptionInternal(
            Exception exc, Object body, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request
    ) {
        return new ResponseEntity<>(new ExceptionRestGenericMessage(status, exc.getMessage()), status);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exc, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request
    ) {
        return new ResponseEntity<>(new ExceptionRestGenericMessage(status, exc.getMessage()), status);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exc, @NonNull HttpHeaders headers, @NonNull HttpStatus status, @NonNull WebRequest request
    ) {
        BindingResult result = exc.getBindingResult();
        List<String> errorList = result
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        return new ResponseEntity<>(new ExceptionRestGenericMessage(status, errorList.toString()), status);
    }
}