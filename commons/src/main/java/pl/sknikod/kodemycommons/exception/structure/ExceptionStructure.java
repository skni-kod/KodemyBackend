package pl.sknikod.kodemycommons.exception.structure;

import org.springframework.http.HttpStatus;

public interface ExceptionStructure {
    HttpStatus getHttpStatus();

    String getMessage();
}