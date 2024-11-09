package pl.sknikod.kodemycommons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

public class Validation400Exception extends InternalError500Exception {
    public Validation400Exception(String pattern, @Nullable Object... args) {
        super(pattern, args);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
