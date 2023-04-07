package pl.sknikod.kodemy.exception.general;

import org.springframework.http.HttpStatus;

public class NotFoundException extends GeneralRuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}