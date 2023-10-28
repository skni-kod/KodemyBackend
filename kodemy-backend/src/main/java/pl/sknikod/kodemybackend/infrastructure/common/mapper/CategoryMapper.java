package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemybackend.infrastructure.category.rest.SingleCategoryResponse;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    SingleCategoryResponse map(Category category);
}
