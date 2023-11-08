package pl.sknikod.kodemyauth.infrastructure.user.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Role;

@Controller
@AllArgsConstructor
public class RoleController implements RoleControllerDefinition {
    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Role.RoleName[]> getRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(Role.RoleName.values());
    }
}
