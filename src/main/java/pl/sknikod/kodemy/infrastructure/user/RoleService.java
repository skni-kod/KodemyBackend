package pl.sknikod.kodemy.infrastructure.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.infrastructure.common.entity.RoleName;

@Service
@AllArgsConstructor
public class RoleService {

    public RoleName[] getRoles() {
        return RoleName.values();
    }

}
