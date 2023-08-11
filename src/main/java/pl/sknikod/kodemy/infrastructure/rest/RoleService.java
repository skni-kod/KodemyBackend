package pl.sknikod.kodemy.infrastructure.rest;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.infrastructure.model.entity.RoleName;

@Service
@AllArgsConstructor
public class RoleService {

    public RoleName[] getRoles() {
        return RoleName.values();
    }

}
