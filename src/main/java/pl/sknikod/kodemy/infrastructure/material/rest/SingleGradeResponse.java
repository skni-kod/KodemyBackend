package pl.sknikod.kodemy.infrastructure.material.rest;

import lombok.Value;
import pl.sknikod.kodemy.infrastructure.common.rest.UserDetails;

@Value
public class SingleGradeResponse {
    Long id;
    Double value;
    UserDetails creator;
}
