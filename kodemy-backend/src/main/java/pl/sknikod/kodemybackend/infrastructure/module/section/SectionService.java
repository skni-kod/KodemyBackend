package pl.sknikod.kodemybackend.infrastructure.module.section;

import lombok.AllArgsConstructor;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.SectionMapper;
import pl.sknikod.kodemybackend.infrastructure.dao.SectionDao;
import pl.sknikod.kodemybackend.infrastructure.module.section.model.SingleSectionResponse;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;

import java.util.List;

@AllArgsConstructor
public class SectionService {
    private final SectionDao sectionDao;
    private final SectionMapper sectionMapper;

    public List<SingleSectionResponse> getAllSections() {
        return sectionDao.findAll()
                .map(sectionMapper::map)
                .getOrElseThrow(() -> new InternalError500Exception());
    }
}
