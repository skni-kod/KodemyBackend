package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Tag;
import pl.sknikod.kodemybackend.infrastructure.tag.rest.TagAddRequest;
import pl.sknikod.kodemybackend.infrastructure.tag.rest.TagAddResponse;

@Mapper(componentModel = "spring")
public interface TagMapper {
    @Mappings(value = {
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "materials", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "lastModifiedBy", ignore = true),
            @Mapping(target = "lastModifiedDate", ignore = true)
    })
    Tag map(TagAddRequest tag);

    TagAddResponse map(Tag tag);
}
