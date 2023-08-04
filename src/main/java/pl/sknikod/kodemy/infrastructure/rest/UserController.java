package pl.sknikod.kodemy.infrastructure.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.infrastructure.model.entity.RoleName;
import pl.sknikod.kodemy.infrastructure.rest.model.UserRoleChangeResponse;

@RestController
@AllArgsConstructor
public class UserController implements UserControllerDefinition {

    private final UserService userService;

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoleName> updateRoles(UserRoleChangeResponse roleResponse) {
        userService.changeRoles(roleResponse.getUserId(), roleResponse.getRoleName());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.getUserRole(roleResponse.getUserId()).getName());
    }
}
