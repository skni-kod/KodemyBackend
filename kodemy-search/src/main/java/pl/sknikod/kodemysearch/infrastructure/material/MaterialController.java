package pl.sknikod.kodemysearch.infrastructure.material;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemysearch.infrastructure.search.SearchService;
import pl.sknikod.kodemysearch.infrastructure.search.rest.SingleMaterialResponse;
import pl.sknikod.kodemysearch.infrastructure.search.rest.SearchFields;

import java.util.Objects;

@RestController
@AllArgsConstructor
public class MaterialController implements MaterialControllerDefinition {
    private final SearchService searchService;

    @Override
    public ResponseEntity<Page<SingleMaterialResponse>> search(int size, int page, String sort, Sort.Direction sortDirection, SearchFields searchFields) {
        var materialResponses = searchService.searchMaterials(
                Objects.isNull(searchFields) ? new SearchFields() : searchFields,
                PageRequest.of(page, size, sortDirection, sort)
        );
        return ResponseEntity.status(HttpStatus.OK).body(materialResponses);
    }
}
