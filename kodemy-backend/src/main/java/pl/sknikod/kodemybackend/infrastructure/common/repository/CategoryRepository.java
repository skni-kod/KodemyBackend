package pl.sknikod.kodemybackend.infrastructure.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
