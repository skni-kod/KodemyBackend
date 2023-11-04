package pl.sknikod.kodemy.exception.structure;

import org.springframework.http.HttpStatus;

public class ValidationException extends ServerProcessingException {
    public ValidationException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
