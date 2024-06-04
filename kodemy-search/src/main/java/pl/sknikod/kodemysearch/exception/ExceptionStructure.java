package pl.sknikod.kodemysearch.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionStructure {
    HttpStatus getHttpStatus();

    String getMessage();
}