package pl.sknikod.kodemy.exception;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    public static void writeBodyResponseForHandler(HttpServletResponse response, ObjectMapper objectMapper, Exception ex, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.getWriter().write(objectMapper.writeValueAsString(new ExceptionRestGenericMessage(status, ex)));
    }
}
