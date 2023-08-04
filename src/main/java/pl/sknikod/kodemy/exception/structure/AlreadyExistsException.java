package pl.sknikod.kodemy.exception.structure;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends ServerProcessingException {
    public AlreadyExistsException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}