package pl.sknikod.kodemysearch.exception.structure;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends ServerProcessingException {
    public <T> AlreadyExistsException(Format runtimeFormat, Class<T> className, String field) {
        super(String.format(runtimeFormat.message, className.getSimpleName(), field));
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }

    @AllArgsConstructor
    public enum Format {
        FIELD("%s: %s already exists");
        private final String message;
    }
}