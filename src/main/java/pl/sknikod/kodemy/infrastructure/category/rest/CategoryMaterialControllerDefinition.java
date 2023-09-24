package pl.sknikod.kodemy.infrastructure.category.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;
import pl.sknikod.kodemy.util.SwaggerResponse;

@RequestMapping("/api/categories")
@SwaggerResponse
@Tag(name = "Category")
public interface CategoryMaterialControllerDefinition {

    @Operation(summary = "Show all Category's materials")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.NotFoundCode404
    @GetMapping("/{categoryId}/materials")
    ResponseEntity<Page<MaterialSearchObject>> getMaterialsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "sort", defaultValue = "createdDate") String sort,
            @RequestParam(value = "sort_direction", defaultValue = "DESC") Sort.Direction sortDirection
    );

    @Operation(summary = "Show Category's details")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.NotFoundCode404
    @GetMapping("{categoryId}")
    ResponseEntity<SingleCategoryResponse> getCategoryDetails(@PathVariable Long categoryId);
}
