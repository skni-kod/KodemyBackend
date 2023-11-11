package pl.sknikod.kodemybackend.infrastructure.common.repository;

import org.springframework.data.repository.CrudRepository;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Author;

public interface AuthorRepository extends CrudRepository<Author, Long> {
}
