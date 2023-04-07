package pl.sknikod.kodemy.util;

import org.springframework.http.HttpStatus;

public interface GeneralExceptionStructure {
    HttpStatus getHttpStatus();
    ExceptionRestGenericMessage getBody();
}