package pl.sknikod.kodemy.infrastructure.rest;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.infrastructure.model.entity.RoleName;

@RestController
@AllArgsConstructor
public class UserController implements UserControllerDefinition {

    private final UserService userService;

    @Override
    @PreAuthorize("isAuthenticated() and hasAuthority('CAN_ASSIGN_ROLES')")
    public void updateRoles(Long userId, RoleName roleName) {
        userService.changeRoles(userId, roleName);
    }
}
