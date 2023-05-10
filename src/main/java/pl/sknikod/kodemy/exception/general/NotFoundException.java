package pl.sknikod.kodemy.exception.general;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ServerProcessingException {
    public NotFoundException(String message) {
        super(message);
    }

    public <T> NotFoundException(Format notFoundFormat, Class<T> className, Long id){
        super(String.format(notFoundFormat.format, className.getSimpleName(),id));
    }
    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    public enum Format {
        entityId("%s not found with id: %d");
        private final String format;
        Format(String format){
            this.format = format;
        }
    }
}