package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import pl.sknikod.kodemybackend.infrastructure.database.Category;
import pl.sknikod.kodemybackend.infrastructure.module.category.model.SingleCategoryResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    SingleCategoryResponse map(Category category);
}
