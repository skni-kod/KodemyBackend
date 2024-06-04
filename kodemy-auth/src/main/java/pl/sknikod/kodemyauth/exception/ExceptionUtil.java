package pl.sknikod.kodemyauth.exception;

import pl.sknikod.kodemyauth.exception.structure.ServerProcessingException;

public class ExceptionUtil {
    private ExceptionUtil() {
    }

    private static RuntimeException getRuntimeOrDefault(Throwable throwable, ServerProcessingException exception) {
        if (throwable instanceof RuntimeException ex) return ex;
        return exception;
    }

    public static RuntimeException throwIfFailure(Throwable throwable) {
        return getRuntimeOrDefault(throwable, new ServerProcessingException());
    }

    public static RuntimeException throwIfFailure(Throwable throwable, String pattern, Object... args) {
        return getRuntimeOrDefault(throwable, new ServerProcessingException(pattern, args));
    }
}
