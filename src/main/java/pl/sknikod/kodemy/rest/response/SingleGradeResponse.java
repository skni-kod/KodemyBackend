package pl.sknikod.kodemy.rest.response;

import lombok.Value;
import pl.sknikod.kodemy.rest.UserDetails;

@Value
public class SingleGradeResponse {
    Long id;
    Double grade;
    UserDetails createdBy;
}
