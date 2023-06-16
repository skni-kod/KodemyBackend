package pl.sknikod.kodemy.infrastructure.rest.model;

import lombok.Value;

@Value
public class SingleGradeResponse {
    Long id;
    Double grade;
    UserDetails creator;
}
