package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Section;
import pl.sknikod.kodemybackend.infrastructure.section.rest.SingleSectionResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SectionMapper {
    List<SingleSectionResponse> map(List<Section> sections);

    SingleSectionResponse map(Section section);
}
