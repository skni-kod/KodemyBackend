package pl.sknikod.kodemyauth.infrastructure.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Role;

@Service
@AllArgsConstructor
public class RoleService {

    public Role.RoleName[] getRoles() {
        return Role.RoleName.values();
    }

}
