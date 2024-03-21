package pl.sknikod.kodemysearch.infrastructure.material.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemysearch.infrastructure.material.MaterialGetUseCase;

import java.util.Objects;

@RestController
@AllArgsConstructor
public class MaterialController implements MaterialControllerDefinition {
    private final MaterialGetUseCase searchService;

    @Override
    public ResponseEntity<Page<MaterialGetUseCase.MaterialPageable>> search(
            int size,
            int page,
            PossibleMaterialSortFields sort,
            Sort.Direction sortDirection,
            MaterialGetUseCase.SearchFields searchFields
    ) {
        var materialResponses = searchService.searchMaterials(
                Objects.isNull(searchFields) ? new MaterialGetUseCase.SearchFields() : searchFields,
                PageRequest.of(page, size, sortDirection, sort.toString())
        );
        return ResponseEntity.status(HttpStatus.OK).body(materialResponses);
    }
}
