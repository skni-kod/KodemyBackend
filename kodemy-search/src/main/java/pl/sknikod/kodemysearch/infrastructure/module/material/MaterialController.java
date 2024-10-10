package pl.sknikod.kodemysearch.infrastructure.module.material;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialSearchFields;
import pl.sknikod.kodemysearch.infrastructure.rest.MaterialControllerDefinition;

import java.util.Objects;

@RestController
@AllArgsConstructor
public class MaterialController implements MaterialControllerDefinition {
    private final MaterialSearchService materialSearchService;

    @Override
    public ResponseEntity<Page<MaterialSearchService.MaterialPageable>> search(
            int size, int page,
            MaterialSearchService.MaterialSortField sortField, Sort.Direction sortDirection,
            MaterialSearchFields searchFields
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialSearchService.search(
                        Objects.requireNonNullElse(searchFields, new MaterialSearchFields()),
                        PageRequest.of(page, size, sortDirection, sortField.getField())
                ));
    }
}
