package pl.sknikod.kodemy.infrastructure.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemy.infrastructure.common.entity.Material;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
}