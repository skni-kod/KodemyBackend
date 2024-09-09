package pl.sknikod.kodemycommon.exception.content;

import pl.sknikod.kodemycommon.exception.InternalError500Exception;

public class ExceptionUtil {
    private static RuntimeException getRuntimeOrDefault(Throwable throwable, InternalError500Exception exception) {
        if (throwable instanceof RuntimeException ex) return ex;
        return exception;
    }

    public static RuntimeException throwIfFailure(Throwable throwable) {
        return getRuntimeOrDefault(throwable, new InternalError500Exception());
    }

    public static RuntimeException throwIfFailure(Throwable throwable, String pattern, Object ...args) {
        return getRuntimeOrDefault(throwable, new InternalError500Exception(pattern, args));
    }
}
