package pl.sknikod.kodemy.infrastructure.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemy.infrastructure.model.entity.Technology;

import java.util.Optional;

@Repository
public interface TechnologyRepository extends JpaRepository<Technology, Long> {
    Optional<Technology> findByName(String name);
}