package pl.sknikod.kodemybackend.infrastructure.section;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Section;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.SectionMapper;
import pl.sknikod.kodemybackend.infrastructure.common.repository.SectionRepository;
import pl.sknikod.kodemybackend.infrastructure.section.rest.SingleSectionResponse;

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
