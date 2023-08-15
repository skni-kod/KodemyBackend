package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.sknikod.kodemy.infrastructure.model.entity.Technology;
import pl.sknikod.kodemy.infrastructure.rest.model.TechnologyAddRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.TechnologyAddResponse;

@Mapper(componentModel = "spring", uses = {
        TechnologyMapper.class
})
public interface TechnologyMapper {
    @Mappings(value = {
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name"),
            @Mapping(target = "materials", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "lastModifiedBy", ignore = true),
            @Mapping(target = "lastModifiedDate", ignore = true)
    })
    Technology map(TechnologyAddRequest tech);

    TechnologyAddResponse map(Technology tech);
}
