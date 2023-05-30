package pl.sknikod.kodemy.exception.general;

import org.springframework.http.HttpStatus;
import pl.sknikod.kodemy.exception.ExceptionRestGenericMessage;
import pl.sknikod.kodemy.exception.ExceptionStructure;

public class ServerProcessingException extends RuntimeException implements ExceptionStructure {

    public ServerProcessingException(String message) {
        super(message);
    }

    public <T> ServerProcessingException(Format runtimeFormat, Class<T> className) {
        super(String.format(runtimeFormat.format, className.getSimpleName()));
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public ExceptionRestGenericMessage getBody() {
        return new ExceptionRestGenericMessage(this.getHttpStatus(), this.getMessage());
    }

    public enum Format {
        processFailed("Failed to process %s");
        private final String format;

        Format(String format) {
            this.format = format;
        }
    }
}
