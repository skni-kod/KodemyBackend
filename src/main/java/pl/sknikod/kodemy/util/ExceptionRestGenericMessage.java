package pl.sknikod.kodemy.util;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionRestGenericMessage {

    private long timeStamp;
    private int status;
    private String message;
    private String description;

}
