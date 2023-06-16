package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sknikod.kodemy.exception.structure.NotFoundException;
import pl.sknikod.kodemy.infrastructure.model.technology.Technology;
import pl.sknikod.kodemy.infrastructure.model.technology.TechnologyRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class TechnologyMapper {
    @Autowired
    protected TechnologyRepository technologyRepository;

    public Set<Technology> map(Set<Long> technologiesIds) {
        return technologiesIds
                .stream()
                .map(id -> technologyRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(NotFoundException.Format.entityId, Technology.class, id))
                )
                .collect(Collectors.toSet());
    }
}
