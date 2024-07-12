package pl.sknikod.kodemyauth.exception.structure;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import pl.sknikod.kodemyauth.exception.ExceptionStructure;

public class OAuth2Exception extends AuthenticationException implements ExceptionStructure {
    public OAuth2Exception(String msg) {
        super(msg);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}