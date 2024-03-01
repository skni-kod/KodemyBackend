package pl.sknikod.kodemysearch.exception;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
public class ExceptionRestGenericMessage {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date timeStamp;
    private int status;
    private String error;
    private String message;

    public ExceptionRestGenericMessage(int status, String error, String message) {
        this.timeStamp = new Date();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ExceptionRestGenericMessage(HttpStatus status, Exception ex) {
        this(status.value(), status.getReasonPhrase(), ex.getMessage());
    }

    public ExceptionRestGenericMessage(HttpStatus status, String message) {
        this(status.value(), status.getReasonPhrase(), message);
    }
}
