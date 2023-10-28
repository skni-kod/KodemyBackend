package pl.sknikod.kodemyauth.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionStructure {
    HttpStatus getHttpStatus();

    ExceptionRestGenericMessage getBody();
}