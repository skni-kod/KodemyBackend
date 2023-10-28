package pl.sknikod.kodemysearch.infrastructure.material;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.sknikod.kodemysearch.infrastructure.search.rest.MaterialResponse;
import pl.sknikod.kodemysearch.infrastructure.search.rest.SearchFields;
import pl.sknikod.kodemysearch.util.SwaggerResponse;

@RequestMapping("/api/materials")
@SwaggerResponse
@Tag(name = "Material")
public interface MaterialControllerDefinition {
    @Operation(summary = "Show all materials")
    @SwaggerResponse.SuccessCode200
    @GetMapping
    ResponseEntity<Page<MaterialResponse>> search(
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "sort", defaultValue = "createdDate") String sort,
            @RequestParam(value = "sort_direction", defaultValue = "DESC") Sort.Direction sortDirection,
            @RequestParam(value = "search_fields", required = false) SearchFields searchFields
    );
}
