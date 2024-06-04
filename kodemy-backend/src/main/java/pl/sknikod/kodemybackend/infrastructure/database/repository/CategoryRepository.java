package pl.sknikod.kodemybackend.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
