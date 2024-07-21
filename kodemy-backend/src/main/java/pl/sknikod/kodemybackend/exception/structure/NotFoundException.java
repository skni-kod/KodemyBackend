package pl.sknikod.kodemybackend.exception.structure;

import org.springframework.lang.Nullable;
import org.springframework.http.HttpStatus;

public class NotFoundException extends ServerProcessingException {
    public NotFoundException(String pattern, @Nullable Object... args) {
        super(pattern, args);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}