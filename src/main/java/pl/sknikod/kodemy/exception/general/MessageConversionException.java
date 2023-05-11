package pl.sknikod.kodemy.exception.general;

import org.springframework.http.HttpStatus;

public class MessageConversionException extends ServerProcessingException {
    public MessageConversionException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
