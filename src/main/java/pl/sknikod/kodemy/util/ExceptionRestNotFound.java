package pl.sknikod.kodemy.util;

public class ExceptionRestNotFound extends RuntimeException{

    public ExceptionRestNotFound(String message) {
        super(message);
    }

    public ExceptionRestNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public ExceptionRestNotFound(Throwable cause) {
        super(cause);
    }
}
