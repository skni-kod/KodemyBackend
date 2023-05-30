package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import pl.sknikod.kodemy.infrastructure.rest.model.response.SingleSectionResponse;
import pl.sknikod.kodemy.infrastructure.model.section.Section;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
    uses = {
        CategoryMapper.class
    }
)
public abstract class SectionMapper {
    public abstract List<SingleSectionResponse> map(List<Section> sections);

    public abstract SingleSectionResponse map(Section section);
}
