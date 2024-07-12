package pl.sknikod.kodemysearch.exception.structure;

import org.jetbrains.annotations.Nullable;
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