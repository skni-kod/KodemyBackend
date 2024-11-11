package pl.sknikod.kodemycommons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

public class NotFound404Exception extends InternalError500Exception {
    public NotFound404Exception(String pattern, @Nullable Object... args) {
        super(pattern, args);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}