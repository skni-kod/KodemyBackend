package pl.sknikod.kodemy.exception.structure;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import pl.sknikod.kodemy.exception.ExceptionRestGenericMessage;
import pl.sknikod.kodemy.exception.ExceptionStructure;

public class ServerProcessingException extends RuntimeException implements ExceptionStructure {

    public ServerProcessingException(String message) {
        super(message);
    }

    public ServerProcessingException() {
        super("Internal error");
    }

    public <T> ServerProcessingException(Format runtimeFormat, Class<T> className) {
        super(String.format(runtimeFormat.message, className.getSimpleName()));
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public ExceptionRestGenericMessage getBody() {
        return new ExceptionRestGenericMessage(this.getHttpStatus(), this.getMessage());
    }

    @AllArgsConstructor
    public enum Format {
        PROCESS_FAILED("Failed to process %s");
        private final String message;
    }
}
