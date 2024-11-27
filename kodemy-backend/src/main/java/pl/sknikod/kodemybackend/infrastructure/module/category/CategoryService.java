package pl.sknikod.kodemybackend.infrastructure.module.category;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.infrastructure.mapper.CategoryMapper;
import pl.sknikod.kodemybackend.infrastructure.store.CategoryStore;
import pl.sknikod.kodemybackend.infrastructure.module.category.model.SingleCategoryResponse;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryStore categoryStore;
    private final CategoryMapper categoryMapper;

    public SingleCategoryResponse showCategoryInfo(Long categoryId) {
        return categoryStore.findById(categoryId)
                .map(categoryMapper::map)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
