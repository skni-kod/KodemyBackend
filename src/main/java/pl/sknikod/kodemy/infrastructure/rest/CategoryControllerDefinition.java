package pl.sknikod.kodemy.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemy.infrastructure.rest.model.response.SingleMaterialResponse;
import pl.sknikod.kodemy.util.SwaggerResponse;

import java.util.Set;

@RequestMapping("/api/categories")
@SwaggerResponse
@Tag(name = "Category")
public interface CategoryControllerDefinition {

    @Operation(summary = "Show all Category's materials")
    @SwaggerResponse.ReadRequest
    @GetMapping("/{categoryId}")
    ResponseEntity<Set<SingleMaterialResponse>> showMaterials(@PathVariable Long categoryId);
}
