package pl.sknikod.kodemybackend.infrastructure.category.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemybackend.util.SwaggerResponse;

@RequestMapping("/api/categories")
@SwaggerResponse
@Tag(name = "Category")
public interface CategoryControllerDefinition {
    @Operation(summary = "Show Category's details")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.NotFoundCode404
    @GetMapping("{categoryId}")
    ResponseEntity<SingleCategoryResponse> getCategoryDetails(@PathVariable Long categoryId);
}
