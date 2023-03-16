package pl.sknikod.kodemy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import pl.sknikod.kodemy.util.ExceptionRestGenericMessage;

public class GeneralException extends RuntimeException {

    @Getter
    private final ExceptionRestGenericMessage exceptionRestGenericMessage;

    public GeneralException(HttpStatus httpStatus, String message) {
        super();
        this.exceptionRestGenericMessage = new ExceptionRestGenericMessage(httpStatus, message);
    }

    public GeneralException(String message) {
        super();
        this.exceptionRestGenericMessage = new ExceptionRestGenericMessage(message);
    }
}