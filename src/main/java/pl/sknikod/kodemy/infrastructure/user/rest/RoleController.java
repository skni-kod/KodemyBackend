package pl.sknikod.kodemy.infrastructure.user.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import pl.sknikod.kodemy.infrastructure.common.entity.RoleName;
import pl.sknikod.kodemy.infrastructure.user.RoleService;

@Controller
@AllArgsConstructor
public class RoleController implements RoleControllerDefinition {

    private final RoleService roleService;

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoleName[]> getRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(roleService.getRoles());
    }
}
