package pl.sknikod.kodemy.infrastructure.rest.model.response;

import lombok.Value;
import pl.sknikod.kodemy.infrastructure.rest.model.UserDetails;

@Value
public class SingleGradeResponse {
    Long id;
    Double grade;
    UserDetails createdBy;
}
