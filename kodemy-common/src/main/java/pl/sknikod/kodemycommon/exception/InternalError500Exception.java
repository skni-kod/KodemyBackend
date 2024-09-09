package pl.sknikod.kodemycommon.exception;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import pl.sknikod.kodemycommon.exception.content.ExceptionMsgPattern;
import pl.sknikod.kodemycommon.exception.structure.ExceptionStructureWrapper;

public class InternalError500Exception extends ExceptionStructureWrapper {
    public InternalError500Exception(@NotNull String pattern, @Nullable Object... args) {
        super(String.format(pattern, args));
    }

    public InternalError500Exception() {
        super(ExceptionMsgPattern.INTERNAL_ERROR);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
