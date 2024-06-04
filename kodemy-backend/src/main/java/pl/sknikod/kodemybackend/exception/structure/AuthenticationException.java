package pl.sknikod.kodemybackend.exception.structure;

import jakarta.validation.constraints.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import pl.sknikod.kodemybackend.exception.ExceptionStructure;

public class AuthenticationException extends org.springframework.security.core.AuthenticationException implements ExceptionStructure {

    public AuthenticationException(@NotNull String pattern, @Nullable Object... args) {
        super(String.format(pattern, args));
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}