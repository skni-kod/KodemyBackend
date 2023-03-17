package pl.sknikod.kodemy.util;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ExceptionRestGenericMessage {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timeStamp;
    private int status;
    private String error;
    private String message;

    public ExceptionRestGenericMessage(){
        this.timeStamp = LocalDateTime.now();
        this.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.error = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
    }

    public ExceptionRestGenericMessage(String message) {
        this();
        this.message = message;
    }

    public ExceptionRestGenericMessage(HttpStatus httpStatus, String message) {
        this.timeStamp = LocalDateTime.now();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
    }
}
