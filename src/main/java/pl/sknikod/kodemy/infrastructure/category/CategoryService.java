package pl.sknikod.kodemy.infrastructure.category;

import lombok.AllArgsConstructor;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.infrastructure.search.MaterialSearchMapper;
import pl.sknikod.kodemy.infrastructure.search.OpenSearchService;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialOpenSearch;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {
    private final OpenSearchService openSearchService;
    private final MaterialSearchMapper materialOpenSearchMapper;

    public List<MaterialOpenSearch> showMaterials(Long categoryId, Integer limit) {
        SearchHit[] searchHits = openSearchService.search(
                OpenSearchService.Info.MATERIAL,
                QueryBuilders.termQuery("categoryId", categoryId),
                limit
        );
        return Arrays.stream(searchHits)
                .map(materialOpenSearchMapper::map)
                .toList();
    }
}
