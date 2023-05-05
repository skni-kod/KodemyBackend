package pl.sknikod.kodemy.exception.general;

import org.springframework.http.HttpStatus;

public class NotFoundException extends GeneralRuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public <T> NotFoundException(NotFoundFormat notFoundFormat, Class<T> className, Long id){
        super(String.format(notFoundFormat.format, className.getSimpleName(),id));
    }
    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    public enum NotFoundFormat{
        entityId("%s not found with id: %d");
        private final String format;
        NotFoundFormat(String format){
            this.format = format;
        }
    }
}