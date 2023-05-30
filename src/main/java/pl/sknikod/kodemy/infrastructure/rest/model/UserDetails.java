package pl.sknikod.kodemy.infrastructure.rest.model;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserDetails {
    Long id;
    String username;
}
