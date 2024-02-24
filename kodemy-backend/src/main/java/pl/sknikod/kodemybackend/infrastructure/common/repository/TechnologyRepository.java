package pl.sknikod.kodemybackend.infrastructure.common.repository;

import io.vavr.control.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Technology;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface TechnologyRepository extends JpaRepository<Technology, Long> {
    Optional<Technology> findByName(String name);

    default Technology findTechnologyById(Long technologyId) {
        return Option.ofOptional(findById(technologyId))
                .getOrElseThrow(() ->
                        new NotFoundException(NotFoundException.Format.ENTITY_ID, Technology.class, technologyId)
                );
    }

    default Set<Technology> findTechnologySetByIds(Set<Long> technologiesIds) {
        return technologiesIds
                .stream()
                .map(this::findTechnologyById)
                .collect(Collectors.toSet());
    }
}