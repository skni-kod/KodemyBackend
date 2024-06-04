package pl.sknikod.kodemybackend.infrastructure.module.section;

import lombok.AllArgsConstructor;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.SectionMapper;
import pl.sknikod.kodemybackend.infrastructure.database.handler.SectionRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.module.section.model.SingleSectionResponse;

import java.util.List;

@AllArgsConstructor
public class SectionUseCase {
    private final SectionRepositoryHandler sectionRepositoryHandler;
    private final SectionMapper sectionMapper;

    public List<SingleSectionResponse> getAllSections() {
        return sectionRepositoryHandler.findAll()
                .map(sectionMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException());
    }
}
