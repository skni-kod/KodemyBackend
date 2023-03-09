package pl.sknikod.kodemy.role;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RoleController {
    private final RoleService roleService;
}