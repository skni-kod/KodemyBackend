package pl.sknikod.kodemybackend.infrastructure.common.repository;

import io.vavr.control.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Type;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {
    default Type findTypeById(Long typeId) {
        return Option.ofOptional(findById(typeId))
                .getOrElseThrow(() ->
                        new NotFoundException(NotFoundException.Format.ENTITY_ID, Type.class, typeId)
                );
    }
}
