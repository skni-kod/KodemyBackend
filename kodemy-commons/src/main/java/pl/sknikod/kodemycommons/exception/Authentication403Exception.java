package pl.sknikod.kodemycommons.exception;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import pl.sknikod.kodemycommons.exception.structure.ExceptionStructure;

public class Authentication403Exception extends org.springframework.security.core.AuthenticationException implements ExceptionStructure {

    public Authentication403Exception(@NotNull String pattern, @Nullable Object... args) {
        super(String.format(pattern, args));
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}