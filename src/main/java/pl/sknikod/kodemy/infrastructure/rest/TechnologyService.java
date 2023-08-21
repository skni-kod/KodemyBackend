package pl.sknikod.kodemy.infrastructure.rest;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.structure.AlreadyExistsException;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.model.entity.Technology;
import pl.sknikod.kodemy.infrastructure.model.repository.TechnologyRepository;
import pl.sknikod.kodemy.infrastructure.rest.mapper.TechnologyMapper;
import pl.sknikod.kodemy.infrastructure.rest.model.TechnologyAddRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.TechnologyAddResponse;

@Service
@AllArgsConstructor
public class TechnologyService {
    private final TechnologyRepository technologyRepository;
    private final TechnologyMapper technologyMapper;

    public TechnologyAddResponse addTechnology(TechnologyAddRequest tech) {
        technologyRepository
                .findByName(tech.getName())
                .ifPresent(found -> {
                    throw new AlreadyExistsException(AlreadyExistsException.Format.FIELD, Technology.class, tech.getName());
                });
        return Option.of(tech)
                .map(technologyMapper::map)
                .peek(technologyRepository::save)
                .map(technologyMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Technology.class));
    }
}
