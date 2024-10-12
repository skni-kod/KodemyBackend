package pl.sknikod.kodemybackend.infrastructure.module.category;

import lombok.AllArgsConstructor;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.CategoryMapper;
import pl.sknikod.kodemybackend.infrastructure.dao.CategoryDao;
import pl.sknikod.kodemybackend.infrastructure.module.category.model.SingleCategoryResponse;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;

@AllArgsConstructor
public class CategoryService {
    private final CategoryDao categoryDao;
    private final CategoryMapper categoryMapper;

    public SingleCategoryResponse showCategoryInfo(Long categoryId) {
        return categoryDao.findById(categoryId)
                .map(categoryMapper::map)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
