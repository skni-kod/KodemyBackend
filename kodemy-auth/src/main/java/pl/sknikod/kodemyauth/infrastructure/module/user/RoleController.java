package pl.sknikod.kodemyauth.infrastructure.module.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemyauth.infrastructure.database.model.Role;
import pl.sknikod.kodemyauth.infrastructure.rest.RoleControllerDefinition;

@RestController
@AllArgsConstructor
public class RoleController implements RoleControllerDefinition {
    @Override
    public ResponseEntity<Role.RoleName[]> getRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(Role.RoleName.values());
    }
}
