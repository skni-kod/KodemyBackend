package pl.sknikod.kodemybackend.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionStructure {
    HttpStatus getHttpStatus();

    ExceptionRestGenericMessage getBody();
}