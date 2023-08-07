package pl.sknikod.kodemy.infrastructure.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemy.infrastructure.model.entity.Role;
import pl.sknikod.kodemy.infrastructure.model.entity.RoleName;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
