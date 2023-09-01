package pl.sknikod.kodemy.infrastructure.category;

import lombok.AllArgsConstructor;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.infrastructure.search.MaterialSearchMapper;
import pl.sknikod.kodemy.infrastructure.search.SearchConfig;
import pl.sknikod.kodemy.infrastructure.search.SearchService;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {
    private final SearchService searchService;
    private final MaterialSearchMapper materialSearchMapper;

    public List<MaterialSearchObject> showMaterials(Long categoryId, Integer limit) {
        SearchHit[] searchHits = searchService.search(
                SearchConfig.MATERIAL_INDEX,
                QueryBuilders.termQuery("categoryId", categoryId),
                limit
        );
        return Arrays.stream(searchHits)
                .map(materialSearchMapper::map)
                .toList();
    }
}
