package pl.sknikod.kodemy.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemy.infrastructure.category.rest.SingleCategoryResponse;
import pl.sknikod.kodemy.infrastructure.common.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    SingleCategoryResponse map(Category category);
}
