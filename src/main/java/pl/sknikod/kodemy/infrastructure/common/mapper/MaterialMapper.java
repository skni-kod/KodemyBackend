package pl.sknikod.kodemy.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemy.infrastructure.common.entity.Material;
import pl.sknikod.kodemy.infrastructure.material.rest.SingleMaterialResponse;

@Mapper(componentModel = "spring")
public interface MaterialMapper {
    SingleMaterialResponse map(Material material);
}
