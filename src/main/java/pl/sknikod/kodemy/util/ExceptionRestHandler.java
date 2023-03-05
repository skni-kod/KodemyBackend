package pl.sknikod.kodemy.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.sknikod.kodemy.exception.ForbiddenCodeException;
import pl.sknikod.kodemy.exception.NotFoundCodeException;
import pl.sknikod.kodemy.exception.OAuth2AuthenticationProcessingException;
import pl.sknikod.kodemy.exception.UnauthorizedCodeException;

@ControllerAdvice
public class ExceptionRestHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionRestGenericMessage> handleException(Exception exc) {
        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage();

        excMessage.setStatus(HttpStatus.BAD_REQUEST.value());
        excMessage.setMessage(exc.getMessage());
        excMessage.setTimeStamp(System.currentTimeMillis());
        excMessage.setDescription("Exception");

        return new ResponseEntity<>(excMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRestGenericMessage> handleException(ForbiddenCodeException exc) {

        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage();

        excMessage.setStatus(HttpStatus.BAD_REQUEST.value());
        excMessage.setMessage(exc.getMessage());
        excMessage.setTimeStamp(System.currentTimeMillis());
        excMessage.setDescription("Forbidden Exception");

        return new ResponseEntity<>(excMessage, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRestGenericMessage> handleException(NotFoundCodeException exc) {

        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage();

        excMessage.setStatus(HttpStatus.BAD_REQUEST.value());
        excMessage.setMessage(exc.getMessage());
        excMessage.setTimeStamp(System.currentTimeMillis());
        excMessage.setDescription("Not Found Exception");

        return new ResponseEntity<>(excMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRestGenericMessage> handleException(OAuth2AuthenticationProcessingException exc) {

        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage();

        excMessage.setStatus(HttpStatus.BAD_REQUEST.value());
        excMessage.setMessage(exc.getMessage());
        excMessage.setTimeStamp(System.currentTimeMillis());
        excMessage.setDescription("OAuth2 Authentication Processing Exception");

        return new ResponseEntity<>(excMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRestGenericMessage> handleException(UnauthorizedCodeException exc) {

        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage();

        excMessage.setStatus(HttpStatus.BAD_REQUEST.value());
        excMessage.setMessage(exc.getMessage());
        excMessage.setTimeStamp(System.currentTimeMillis());
        excMessage.setDescription("Unauthorized Exception");

        return new ResponseEntity<>(excMessage, HttpStatus.UNAUTHORIZED);
    }
}
