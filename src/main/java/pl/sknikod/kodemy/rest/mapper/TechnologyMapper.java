package pl.sknikod.kodemy.rest.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.exception.general.NotFoundException;
import pl.sknikod.kodemy.technology.Technology;
import pl.sknikod.kodemy.technology.TechnologyRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
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
