package pl.sknikod.kodemyauth.infrastructure.module.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemyauth.infrastructure.database.model.Role;
import pl.sknikod.kodemyauth.infrastructure.database.model.RoleRepository;
import pl.sknikod.kodemyauth.infrastructure.rest.RoleControllerDefinition;

@RestController
@AllArgsConstructor
public class RoleController implements RoleControllerDefinition {

    private final RoleRepository roleRepository;

    @Override
    public ResponseEntity<String[]> getRoles() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(roleRepository.findAll().stream().map(Role::getName).toArray(String[]::new));
    }
}
