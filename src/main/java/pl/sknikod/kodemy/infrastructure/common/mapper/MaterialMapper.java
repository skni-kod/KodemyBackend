package pl.sknikod.kodemy.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.sknikod.kodemy.infrastructure.common.entity.Material;
import pl.sknikod.kodemy.infrastructure.material.rest.SingleMaterialResponse;

@Mapper(componentModel = "spring")
public interface MaterialMapper {

    @Mapping(target = "averageGrade", ignore = true)
    @Mapping(target = "creator", source = "user")
    SingleMaterialResponse map(Material material);
}
