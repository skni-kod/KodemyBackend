package pl.sknikod.kodemy.infrastructure.category.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;
import pl.sknikod.kodemy.util.SwaggerResponse;

import java.util.List;

@RequestMapping("/api/categories")
@SwaggerResponse
@Tag(name = "Category")
public interface CategoryMaterialControllerDefinition {

    @Operation(summary = "Show all Category's materials")
    @SwaggerResponse.SuccessCode
    @SwaggerResponse.NotFoundCode
    @GetMapping("/{categoryId}/materials")
    ResponseEntity<List<MaterialSearchObject>> getMaterialsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page
    );
}
