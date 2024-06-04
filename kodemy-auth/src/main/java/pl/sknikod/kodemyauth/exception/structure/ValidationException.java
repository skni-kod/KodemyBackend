package pl.sknikod.kodemyauth.exception.structure;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

public class ValidationException extends ServerProcessingException {
    public ValidationException(String pattern, @Nullable Object... args) {
        super(pattern, args);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
