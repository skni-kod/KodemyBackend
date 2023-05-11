package pl.sknikod.kodemy.section;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.general.ServerProcessingException;
import pl.sknikod.kodemy.rest.mapper.SectionMapper;
import pl.sknikod.kodemy.rest.response.SingleSectionResponse;

import java.util.List;

@Service
@AllArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;
    private final SectionMapper sectionMapper;

    public List<SingleSectionResponse> getAllSections() {
        return Option.of(sectionRepository.findAllWithFetchCategories())
                .map(sectionMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.processFailed, Section.class));
    }
}
