package pl.sknikod.kodemy.infrastructure.rest;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.model.section.Section;
import pl.sknikod.kodemy.infrastructure.model.section.SectionRepository;
import pl.sknikod.kodemy.infrastructure.rest.mapper.SectionMapper;
import pl.sknikod.kodemy.infrastructure.rest.model.SingleSectionResponse;

import java.util.List;

@Service
@AllArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;
    private final SectionMapper sectionMapper;

    public List<SingleSectionResponse> getAllSections() {
        return Option.of(sectionRepository.findAllWithFetchCategories())
                .map(sectionMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Section.class));
    }
}
