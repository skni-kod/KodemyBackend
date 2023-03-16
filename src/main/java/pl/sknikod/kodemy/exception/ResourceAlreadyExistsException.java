package pl.sknikod.kodemy.exception;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends GeneralException {

    public ResourceAlreadyExistsException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}