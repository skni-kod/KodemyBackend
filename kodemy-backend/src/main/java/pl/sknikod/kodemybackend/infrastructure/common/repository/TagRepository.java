package pl.sknikod.kodemybackend.infrastructure.common.repository;

import io.vavr.control.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Tag;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    default Tag findTagById(Long tagId) {
        return Option.ofOptional(findById(tagId))
                .getOrElseThrow(() ->
                        new NotFoundException(NotFoundException.Format.ENTITY_ID, Tag.class, tagId)
                );
    }

    Set<Tag> findTagsByIdIn(List<Long> tagIds);
}