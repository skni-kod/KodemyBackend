package pl.sknikod.kodemy.rest;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserDetails {
    Long id;
    String username;
}
