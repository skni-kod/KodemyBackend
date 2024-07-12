package pl.sknikod.kodemybackend.infrastructure.module.category;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.exception.ExceptionUtil;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.CategoryMapper;
import pl.sknikod.kodemybackend.infrastructure.database.handler.CategoryRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.module.category.model.SingleCategoryResponse;

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
