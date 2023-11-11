package pl.sknikod.kodemybackend.infrastructure.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
