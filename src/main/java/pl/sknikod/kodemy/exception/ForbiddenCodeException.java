package pl.sknikod.kodemy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenCodeException extends RuntimeException{
    public ForbiddenCodeException(String message) {
        super(message);
    }

    public ForbiddenCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}