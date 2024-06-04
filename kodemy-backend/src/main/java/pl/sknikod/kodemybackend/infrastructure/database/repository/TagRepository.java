package pl.sknikod.kodemybackend.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Tag;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    Set<Tag> findTagsByIdIn(Collection<Long> tagIds);

    boolean existsByName(@NonNull String name);
}