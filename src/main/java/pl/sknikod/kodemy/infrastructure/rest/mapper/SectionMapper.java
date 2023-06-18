package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemy.infrastructure.model.section.Section;
import pl.sknikod.kodemy.infrastructure.rest.model.SingleSectionResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SectionMapper {
    List<SingleSectionResponse> map(List<Section> sections);

    SingleSectionResponse map(Section section);
}
