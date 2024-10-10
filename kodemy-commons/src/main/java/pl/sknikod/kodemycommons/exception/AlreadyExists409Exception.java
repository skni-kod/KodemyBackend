package pl.sknikod.kodemycommons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

public class AlreadyExists409Exception extends InternalError500Exception {
    public AlreadyExists409Exception(String pattern, @Nullable Object... args) {
        super(pattern, args);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}