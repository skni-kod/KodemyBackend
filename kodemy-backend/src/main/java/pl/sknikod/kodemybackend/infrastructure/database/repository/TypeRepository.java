package pl.sknikod.kodemybackend.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Type;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {
}
