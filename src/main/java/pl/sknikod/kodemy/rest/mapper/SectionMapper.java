package pl.sknikod.kodemy.rest.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import pl.sknikod.kodemy.rest.response.SingleSectionResponse;
import pl.sknikod.kodemy.section.Section;

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
