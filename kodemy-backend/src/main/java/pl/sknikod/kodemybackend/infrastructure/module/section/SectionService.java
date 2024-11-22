package pl.sknikod.kodemybackend.infrastructure.module.section;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.infrastructure.mapper.SectionMapper;
import pl.sknikod.kodemybackend.infrastructure.store.SectionStore;
import pl.sknikod.kodemybackend.infrastructure.module.section.model.SingleSectionResponse;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;

import java.util.List;

@Service
@AllArgsConstructor
public class SectionService {
    private final SectionStore sectionStore;
    private final SectionMapper sectionMapper;

    public List<SingleSectionResponse> getAllSections() {
        return sectionStore.findAll()
                .map(sectionMapper::map)
                .getOrElseThrow(() -> new InternalError500Exception());
    }
}
