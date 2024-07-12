package pl.sknikod.kodemysearch.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialSearchUseCase;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialSearchFields;
import pl.sknikod.kodemysearch.util.web.SwaggerResponse;

@RequestMapping("/api/materials")
@SwaggerResponse
@Tag(name = "Material")
public interface MaterialControllerDefinition {
    @Operation(summary = "Show all materials")
    @SwaggerResponse.SuccessCode200
    @GetMapping
    ResponseEntity<Page<MaterialSearchUseCase.MaterialPageable>> search(
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "sortField", defaultValue = "CREATED_DATE") MaterialSearchUseCase.MaterialSortField sortField,
            @RequestParam(value = "sort_direction", defaultValue = "DESC") Sort.Direction sortDirection,
            @Parameter(description = MaterialSearchFields.SEARCH_FIELDS_DOC)
            @RequestParam(value = "search_fields", required = false) MaterialSearchFields searchFields
    );
}
