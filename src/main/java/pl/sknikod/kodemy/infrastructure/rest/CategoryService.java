package pl.sknikod.kodemy.infrastructure.rest;

import lombok.AllArgsConstructor;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialOpenSearch;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {
    private final OpenSearchService openSearchService;
    private final OpenSearchService.MaterialOpenSearchMapper materialOpenSearchMapper;

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
