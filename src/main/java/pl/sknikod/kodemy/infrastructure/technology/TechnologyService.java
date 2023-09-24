package pl.sknikod.kodemy.infrastructure.technology;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.structure.AlreadyExistsException;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.common.entity.Technology;
import pl.sknikod.kodemy.infrastructure.common.mapper.TechnologyMapper;
import pl.sknikod.kodemy.infrastructure.common.repository.TechnologyRepository;
import pl.sknikod.kodemy.infrastructure.technology.rest.TechnologyAddRequest;
import pl.sknikod.kodemy.infrastructure.technology.rest.TechnologyAddResponse;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<TechnologyAddResponse> showTechnologies() {
        return technologyRepository
                .findAll()
                .parallelStream()
                .map(technologyMapper::map)
                .collect(Collectors.toList());
    }
}
