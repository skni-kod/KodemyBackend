package pl.sknikod.kodemy.util;

import lombok.NonNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.sknikod.kodemy.exception.ForbiddenException;
import pl.sknikod.kodemy.exception.NotFoundException;
import pl.sknikod.kodemy.exception.OAuth2AuthenticationProcessingException;
import pl.sknikod.kodemy.exception.UnauthorizedException;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@ControllerAdvice
public class ExceptionRestHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exc, Object body, @NonNull HttpHeaders headers, @NotNull HttpStatus status, WebRequest request
    ) {
        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage(
                Instant.now(),
                status.value(),
                "Internal Server Error",
                exc.getMessage()
        );
        return new ResponseEntity<>(excMessage, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exc, @NonNull HttpHeaders headers, HttpStatus status, @NonNull WebRequest request
    ) {
        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage(
                Instant.now(),
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
                Instant.now(),
                status.value(),
                "Method Argument Not Valid",
                errorList.toString()
        );
        return new ResponseEntity<>(excMessage, status);
    }


    @ExceptionHandler
    public ResponseEntity<Object> handleAllException(
            Exception exc
    ) {
        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                exc.getMessage()
        );
        return new ResponseEntity<>(excMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRestGenericMessage> handleForbiddenException(
            ForbiddenException exc
    ) {
        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage(
                Instant.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                exc.getMessage()
        );

        return new ResponseEntity<>(excMessage, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRestGenericMessage> handleNotFoundException(
            NotFoundException exc
    ) {
        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exc.getMessage()
        );

        return new ResponseEntity<>(excMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRestGenericMessage> handleOAuth2AuthenticationProcessingException(
            OAuth2AuthenticationProcessingException exc
    ) {
        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exc.getMessage()
        );

        return new ResponseEntity<>(excMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRestGenericMessage> handleUnauthorizedException(
            UnauthorizedException exc
    ) {
        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                exc.getMessage()
        );
        return new ResponseEntity<>(excMessage, HttpStatus.UNAUTHORIZED);
    }
}