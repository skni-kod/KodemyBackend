package pl.sknikod.kodemy.exception.structure;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import pl.sknikod.kodemy.exception.ExceptionRestGenericMessage;
import pl.sknikod.kodemy.exception.ExceptionStructure;

public class OAuth2Exception extends AuthenticationException implements ExceptionStructure {
    public OAuth2Exception(String msg) {
        super(msg);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public ExceptionRestGenericMessage getBody() {
        return new ExceptionRestGenericMessage(this.getHttpStatus(), this.getMessage());
    }
}