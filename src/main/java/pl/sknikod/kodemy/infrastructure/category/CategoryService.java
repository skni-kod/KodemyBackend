package pl.sknikod.kodemy.infrastructure.category;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.structure.NotFoundException;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.category.rest.SingleCategoryResponse;
import pl.sknikod.kodemy.infrastructure.common.entity.Category;
import pl.sknikod.kodemy.infrastructure.common.mapper.CategoryMapper;
import pl.sknikod.kodemy.infrastructure.common.repository.CategoryRepository;
import pl.sknikod.kodemy.infrastructure.search.MaterialSearchMapper;
import pl.sknikod.kodemy.infrastructure.search.SearchConfig;
import pl.sknikod.kodemy.infrastructure.search.SearchService;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {
    private final SearchService searchService;
    private final MaterialSearchMapper materialSearchMapper;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    public List<MaterialSearchObject> showMaterials(Long categoryId, int size, int page) {
        SearchHit[] searchHits = searchService.search(
                SearchConfig.MATERIAL_INDEX,
                PageRequest.of(page, size),
                sourceBuilder -> sourceBuilder.query(QueryBuilders.termQuery("categoryId", categoryId))
        );
        return Arrays.stream(searchHits)
                .map(materialSearchMapper::map)
                .toList();
    }

    @Transactional
    public SingleCategoryResponse showCategoryInfo(Long categoryId) {
        Category category = Option.ofOptional(categoryRepository.findById(categoryId))
                .getOrElseThrow(() -> new NotFoundException(NotFoundException.Format.ENTITY_ID, Category.class, categoryId));
        return Option.of(categoryMapper.map(category))
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Category.class));
    }
}
