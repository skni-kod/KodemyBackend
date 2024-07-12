package pl.sknikod.kodemysearch.exception.structure;

import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends ServerProcessingException {
    public AlreadyExistsException(String pattern, @Nullable Object... args) {
        super(pattern, args);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}