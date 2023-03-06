package pl.sknikod.kodemy.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import pl.sknikod.kodemy.exception.ForbiddenCodeException;
import pl.sknikod.kodemy.exception.NotFoundCodeException;
import pl.sknikod.kodemy.exception.OAuth2AuthenticationProcessingException;
import pl.sknikod.kodemy.exception.UnauthorizedCodeException;

@ControllerAdvice
public class ExceptionRestHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionRestGenericMessage> handleException(Exception exc, WebRequest request) {
        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage(System.currentTimeMillis(),
                HttpStatus.BAD_REQUEST.value(),
                exc.getMessage() != null ? exc.getMessage() : HttpStatus.BAD_REQUEST.getReasonPhrase(),
                request.getDescription(false));

        return new ResponseEntity<>(excMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRestGenericMessage> handleException(ForbiddenCodeException exc, WebRequest request) {
        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage(System.currentTimeMillis(),
                HttpStatus.FORBIDDEN.value(),
                exc.getMessage() != null ? exc.getMessage() : HttpStatus.FORBIDDEN.getReasonPhrase(),
                request.getDescription(false));

        return new ResponseEntity<>(excMessage, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRestGenericMessage> handleNotFoundException(NotFoundCodeException exc, WebRequest request) {
        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage(System.currentTimeMillis(),
                HttpStatus.NOT_FOUND.value(),
                exc.getMessage() != null ? exc.getMessage() : HttpStatus.NOT_FOUND.getReasonPhrase(),
                request.getDescription(false));

        return new ResponseEntity<>(excMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRestGenericMessage> handleException(OAuth2AuthenticationProcessingException exc, WebRequest request) {
        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage(System.currentTimeMillis(),
                HttpStatus.BAD_REQUEST.value(),
                exc.getMessage() != null ? exc.getMessage() : HttpStatus.BAD_REQUEST.getReasonPhrase(),
                request.getDescription(false));

        return new ResponseEntity<>(excMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRestGenericMessage> handleUnauthorizedException(UnauthorizedCodeException exc, WebRequest request) {
        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage(System.currentTimeMillis(),
                HttpStatus.UNAUTHORIZED.value(),
                exc.getMessage() != null ? exc.getMessage() : HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                request.getDescription(false));

        return new ResponseEntity<>(excMessage, HttpStatus.UNAUTHORIZED);
    }
}
