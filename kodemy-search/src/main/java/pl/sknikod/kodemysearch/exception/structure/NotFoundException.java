package pl.sknikod.kodemysearch.exception.structure;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

public class NotFoundException extends ServerProcessingException {
    public NotFoundException(String message) {
        super(message);
    }

    public <T> NotFoundException(Format runtimeFormat, Class<T> className, Long id) {
        super(String.format(runtimeFormat.message, className.getSimpleName(), id));
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @AllArgsConstructor
    public enum Format {
        ENTITY_ID("%s not found with id: %d");
        private final String message;
    }
}