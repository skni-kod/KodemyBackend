package pl.sknikod.kodemy.exception.general;

import org.springframework.http.HttpStatus;
import pl.sknikod.kodemy.util.ExceptionRestGenericMessage;
import pl.sknikod.kodemy.util.GeneralExceptionStructure;

public class GeneralRuntimeException extends RuntimeException implements GeneralExceptionStructure {

    public GeneralRuntimeException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public ExceptionRestGenericMessage getBody() {
        return new ExceptionRestGenericMessage(this.getHttpStatus(), this.getMessage());
    }
}
