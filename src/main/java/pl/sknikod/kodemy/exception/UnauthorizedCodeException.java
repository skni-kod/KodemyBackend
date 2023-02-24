package pl.sknikod.kodemy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedCodeException extends RuntimeException {
    public UnauthorizedCodeException(String message) {
        super(message);
    }

    public UnauthorizedCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}