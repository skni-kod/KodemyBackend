package pl.sknikod.kodemy.infrastructure.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialOpenSearch;
import pl.sknikod.kodemy.util.SwaggerResponse;

import java.util.List;

@RequestMapping("/api/categories")
@SwaggerResponse
@Tag(name = "Category")
public interface CategoryMaterialControllerDefinition {

    @Operation(summary = "Show all Category's materials")
    @SwaggerResponse.ReadRequest
    @GetMapping("/{categoryId}/materials")
    ResponseEntity<List<MaterialOpenSearch>> getMaterialsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false, defaultValue = "10") Integer limit
    );
}
