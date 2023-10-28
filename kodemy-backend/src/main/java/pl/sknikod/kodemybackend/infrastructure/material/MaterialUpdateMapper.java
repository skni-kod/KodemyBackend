package pl.sknikod.kodemybackend.infrastructure.material;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.material.rest.MaterialUpdateRequest;
import pl.sknikod.kodemybackend.infrastructure.material.rest.MaterialUpdateResponse;

@Mapper(componentModel = "spring")
public interface MaterialUpdateMapper {
    @Mappings(value = {
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "active", constant = "true"),
            @Mapping(target = "status", constant = "PENDING"),
            @Mapping(target = "type", ignore = true),
            @Mapping(target = "category", ignore = true),
            @Mapping(target = "technologies", ignore = true),
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "grades", ignore = true),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "lastModifiedBy", ignore = true),
            @Mapping(target = "lastModifiedDate", ignore = true)
    })
    Material map(MaterialUpdateRequest body);

    MaterialUpdateResponse map(Material material);
}
