package pl.sknikod.kodemy.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionRestHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionRestGenericMessage> handleException(Exception exc){
        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage();

        excMessage.setStatus(HttpStatus.BAD_REQUEST.value());
        excMessage.setMessage(exc.getMessage());
        excMessage.setTimeStamp(System.currentTimeMillis());
        excMessage.setDescription("Exception");

        return new ResponseEntity<>(excMessage,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRestGenericMessage> handleException(ExceptionRestNotFound exc){

        ExceptionRestGenericMessage excMessage = new ExceptionRestGenericMessage();

        excMessage.setStatus(HttpStatus.BAD_REQUEST.value());
        excMessage.setMessage(exc.getMessage());
        excMessage.setTimeStamp(System.currentTimeMillis());
        excMessage.setDescription("Not Found Exception");

        return new ResponseEntity<>(excMessage,HttpStatus.NOT_FOUND);
    }
}
