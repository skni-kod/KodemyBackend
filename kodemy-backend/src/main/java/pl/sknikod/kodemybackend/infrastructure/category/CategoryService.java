package pl.sknikod.kodemybackend.infrastructure.category;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.category.rest.SingleCategoryResponse;
import pl.sknikod.kodemybackend.infrastructure.common.EntityDao;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Category;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.CategoryMapper;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryMapper categoryMapper;
    private final EntityDao entityDao;

    @Transactional
    public SingleCategoryResponse showCategoryInfo(Long categoryId) {
        return Option.of(categoryMapper.map(entityDao.findCategoryById(categoryId)))
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Category.class));
    }
}
