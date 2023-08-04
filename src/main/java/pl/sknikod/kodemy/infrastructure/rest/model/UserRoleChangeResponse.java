package pl.sknikod.kodemy.infrastructure.rest.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pl.sknikod.kodemy.infrastructure.model.entity.RoleName;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserRoleChangeResponse {
    Long userId;
    RoleName roleName;
}