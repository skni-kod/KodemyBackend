package pl.sknikod.kodemy.infrastructure.category;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.structure.NotFoundException;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.category.rest.SingleCategoryResponse;
import pl.sknikod.kodemy.infrastructure.common.entity.Category;
import pl.sknikod.kodemy.infrastructure.common.mapper.CategoryMapper;
import pl.sknikod.kodemy.infrastructure.common.repository.CategoryRepository;
import pl.sknikod.kodemy.infrastructure.search.rest.SearchFields;
import pl.sknikod.kodemy.infrastructure.search.SearchService;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class CategoryService {
    private final SearchService searchService;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    public Page<MaterialSearchObject> showMaterials(Long categoryId, int size, int page, String sortField, Sort.Direction sortDirection) {
        SearchFields searchFields = new SearchFields();
        searchFields.setCategoryId(categoryId);
        return searchService.searchMaterials(searchFields, PageRequest.of(page, size, sortDirection, sortField));
    }

    @Transactional
    public SingleCategoryResponse showCategoryInfo(Long categoryId) {
        Category category = Option.ofOptional(categoryRepository.findById(categoryId))
                .getOrElseThrow(() -> new NotFoundException(NotFoundException.Format.ENTITY_ID, Category.class, categoryId));
        return Option.of(categoryMapper.map(category))
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Category.class));
    }
}
