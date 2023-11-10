package pl.sknikod.kodemybackend.infrastructure.category;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.category.rest.SingleCategoryResponse;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Category;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.CategoryMapper;
import pl.sknikod.kodemybackend.infrastructure.common.repository.CategoryRepository;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Transactional
    public SingleCategoryResponse showCategoryInfo(Long categoryId) {
        return Option.ofOptional(categoryRepository.findById(categoryId))
                .onEmpty(() -> {throw new NotFoundException(NotFoundException.Format.ENTITY_ID, Category.class, categoryId);})
                .map(categoryMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Category.class));
    }
}
