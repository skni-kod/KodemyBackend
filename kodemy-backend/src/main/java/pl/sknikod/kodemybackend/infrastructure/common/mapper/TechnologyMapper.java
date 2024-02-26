package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Technology;
import pl.sknikod.kodemybackend.infrastructure.technology.rest.TechnologyAddRequest;
import pl.sknikod.kodemybackend.infrastructure.technology.rest.TechnologyAddResponse;

@Mapper(componentModel = "spring")
public interface TechnologyMapper {
    @Mappings(value = {
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "materials", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "lastModifiedBy", ignore = true),
            @Mapping(target = "lastModifiedDate", ignore = true)
    })
    Technology map(TechnologyAddRequest tech);

    TechnologyAddResponse map(Technology tech);
}
