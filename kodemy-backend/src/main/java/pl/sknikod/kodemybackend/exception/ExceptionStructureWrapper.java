package pl.sknikod.kodemybackend.exception;

import jakarta.validation.constraints.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ExceptionStructureWrapper extends RuntimeException implements ExceptionStructure {
    public ExceptionStructureWrapper(@NotNull String pattern, @Nullable Object... args) {
        super(String.format(pattern, args));
    }
}
