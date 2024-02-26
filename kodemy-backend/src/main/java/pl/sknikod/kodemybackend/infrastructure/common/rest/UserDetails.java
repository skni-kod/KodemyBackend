package pl.sknikod.kodemybackend.infrastructure.common.rest;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserDetails {
    Long id;
    String username;
}
