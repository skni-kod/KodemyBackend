package pl.sknikod.kodemy.util;

import org.springframework.http.HttpStatus;

public interface ExceptionStructure {
    HttpStatus getHttpStatus();
    ExceptionRestGenericMessage getBody();
}