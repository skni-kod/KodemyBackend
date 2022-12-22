package pl.sknikod.kodemy.role;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
}