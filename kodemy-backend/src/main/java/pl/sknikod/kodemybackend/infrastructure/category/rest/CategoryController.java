package pl.sknikod.kodemybackend.infrastructure.category.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemybackend.infrastructure.category.CategoryService;

@RestController
@AllArgsConstructor
public class CategoryController implements CategoryControllerDefinition {
    private final CategoryService categoryService;

    public ResponseEntity<SingleCategoryResponse> getCategoryDetails(Long categoryId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                categoryService.showCategoryInfo(categoryId)
        );
    }
}
