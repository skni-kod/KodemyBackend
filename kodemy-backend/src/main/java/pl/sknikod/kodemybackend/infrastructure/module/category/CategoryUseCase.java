package pl.sknikod.kodemybackend.infrastructure.module.category;

import lombok.AllArgsConstructor;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.CategoryMapper;
import pl.sknikod.kodemybackend.infrastructure.database.handler.CategoryRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.module.category.model.SingleCategoryResponse;
import pl.sknikod.kodemycommon.exception.content.ExceptionUtil;

@AllArgsConstructor
public class CategoryUseCase {
    private final CategoryRepositoryHandler categoryRepositoryHandler;
    private final CategoryMapper categoryMapper;

    public SingleCategoryResponse showCategoryInfo(Long categoryId) {
        return categoryRepositoryHandler.findById(categoryId)
                .map(categoryMapper::map)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
