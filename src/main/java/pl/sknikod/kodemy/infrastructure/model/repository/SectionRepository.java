package pl.sknikod.kodemy.infrastructure.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemy.infrastructure.model.entity.Section;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    @Query("SELECT DISTINCT s FROM Section s LEFT JOIN FETCH s.categories c ORDER BY s.id, c.id ASC")
    List<Section> findAllWithFetchCategories();
}