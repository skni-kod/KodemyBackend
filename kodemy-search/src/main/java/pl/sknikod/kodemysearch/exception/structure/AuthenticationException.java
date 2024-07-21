package pl.sknikod.kodemysearch.exception.structure;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.http.HttpStatus;
import pl.sknikod.kodemysearch.exception.ExceptionStructure;

public class AuthenticationException extends org.springframework.security.core.AuthenticationException implements ExceptionStructure {

    public AuthenticationException(@NotNull String pattern, @Nullable Object... args) {
        super(String.format(pattern, args));
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}