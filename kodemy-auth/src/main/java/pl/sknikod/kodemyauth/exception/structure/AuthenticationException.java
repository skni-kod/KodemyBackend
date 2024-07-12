package pl.sknikod.kodemyauth.exception.structure;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import pl.sknikod.kodemyauth.exception.ExceptionStructure;

public class AuthenticationException extends org.springframework.security.core.AuthenticationException implements ExceptionStructure {

    public AuthenticationException(@NotNull String pattern, @Nullable Object... args) {
        super(String.format(pattern, args));
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}