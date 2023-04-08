package pl.sknikod.kodemy.rest.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.category.Category;
import pl.sknikod.kodemy.category.CategoryRepository;
import pl.sknikod.kodemy.exception.general.NotFoundException;

@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public abstract class CategoryMapper {
    @Autowired
    private CategoryRepository categoryRepository;

    public Category map(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }
}
