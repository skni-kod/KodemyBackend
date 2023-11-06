package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.material.rest.SingleMaterialResponse;

@Mapper(componentModel = "spring")
public interface MaterialMapper {

    @Mappings(value = {
            @Mapping(target = "averageGrade", ignore = true),
            @Mapping(target = "gradeStats", ignore = true),
            @Mapping(target = "creator.id", source = "author.id"),
            @Mapping(target = "creator.username", source = "author.name")
    })
    SingleMaterialResponse map(Material material);
}
