package pl.sknikod.kodemycommon.exception.content;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
public class ExceptionBody {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date timeStamp;
    private int status;
    private String error;
    private String message;

    public ExceptionBody(int status, String error, String message) {
        this.timeStamp = new Date();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ExceptionBody(HttpStatus status, Exception ex) {
        this(status.value(), status.getReasonPhrase(), ex.getMessage());
    }

    public ExceptionBody(HttpStatus status, String message) {
        this(status.value(), status.getReasonPhrase(), message);
    }
}
