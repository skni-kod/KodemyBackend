package pl.sknikod.kodemy.util;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionRestGenericMessage {

    private Instant timeStamp;
    private int status;
    private String error;
    private String message;

}
