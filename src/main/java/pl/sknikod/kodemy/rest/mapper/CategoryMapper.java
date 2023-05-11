package pl.sknikod.kodemy.rest.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.category.Category;
import pl.sknikod.kodemy.category.CategoryRepository;
import pl.sknikod.kodemy.exception.general.NotFoundException;
import pl.sknikod.kodemy.rest.response.SingleSectionResponse;

import java.util.List;
import java.util.Set;

@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public abstract class CategoryMapper {
    @Autowired
    private CategoryRepository categoryRepository;

    public Category map(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundException.Format.entityId, Category.class, id));
    }

    public abstract List<SingleSectionResponse.CategoryDetails> map(Set<Category> categories);

    public abstract SingleSectionResponse.CategoryDetails map(Category category);
}
