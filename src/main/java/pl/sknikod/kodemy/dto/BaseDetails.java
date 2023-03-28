package pl.sknikod.kodemy.dto;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
@Setter
@Hidden
public class BaseDetails {
    Long id;
    String name;
}
