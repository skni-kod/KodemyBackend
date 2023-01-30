package pl.sknikod.kodemy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundCodeException extends RuntimeException {
    public NotFoundCodeException(String message) {
        super(message);
    }

    public NotFoundCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}