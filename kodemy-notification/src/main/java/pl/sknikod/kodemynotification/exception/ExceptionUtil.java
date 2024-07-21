package pl.sknikod.kodemynotification.exception;

import pl.sknikod.kodemynotification.exception.structure.ServerProcessingException;

public class ExceptionUtil {
    private static RuntimeException getRuntimeOrDefault(Throwable throwable, ServerProcessingException exception) {
        if (throwable instanceof RuntimeException ex) return ex;
        return exception;
    }

    public static RuntimeException throwIfFailure(Throwable throwable) {
        return getRuntimeOrDefault(throwable, new ServerProcessingException());
    }

    public static RuntimeException throwIfFailure(Throwable throwable, String pattern, Object ...args) {
        return getRuntimeOrDefault(throwable, new ServerProcessingException(pattern, args));
    }
}
