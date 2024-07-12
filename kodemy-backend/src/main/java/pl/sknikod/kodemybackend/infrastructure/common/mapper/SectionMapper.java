package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Section;
import pl.sknikod.kodemybackend.infrastructure.module.section.model.SingleSectionResponse;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SectionMapper {
    List<SingleSectionResponse> map(List<Section> sections);

    SingleSectionResponse map(Section section);
}
