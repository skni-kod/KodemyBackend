package pl.sknikod.kodemybackend.infrastructure.technology;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.exception.structure.AlreadyExistsException;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Technology;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.TechnologyMapper;
import pl.sknikod.kodemybackend.infrastructure.common.repository.TechnologyRepository;
import pl.sknikod.kodemybackend.infrastructure.technology.rest.TechnologyAddRequest;
import pl.sknikod.kodemybackend.infrastructure.technology.rest.TechnologyAddResponse;

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
