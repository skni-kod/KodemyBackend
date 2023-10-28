package pl.sknikod.kodemybackend.infrastructure.material.rest;

import lombok.Value;
import pl.sknikod.kodemybackend.infrastructure.common.rest.UserDetails;

@Value
public class SingleGradeResponse {
    Long id;
    Double value;
    UserDetails creator;
}
