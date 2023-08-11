package pl.sknikod.kodemy.infrastructure.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import pl.sknikod.kodemy.infrastructure.model.entity.RoleName;

@Controller
@AllArgsConstructor
public class RoleController implements RoleControllerDefinition {

    private final RoleService roleService;

    @Override
    public ResponseEntity<RoleName[]> getRoles() {
        return ResponseEntity.ok(roleService.getRoles());
    }
}
