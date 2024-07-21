package pl.sknikod.kodemybackend.exception.structure;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.http.HttpStatus;
import pl.sknikod.kodemybackend.exception.ExceptionPattern;
import pl.sknikod.kodemybackend.exception.ExceptionStructureWrapper;

public class ServerProcessingException extends ExceptionStructureWrapper {
    public ServerProcessingException(@NotNull String pattern, @Nullable Object... args) {
        super(String.format(pattern, args));
    }

    public ServerProcessingException() {
        super(ExceptionPattern.INTERNAL_ERROR);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
