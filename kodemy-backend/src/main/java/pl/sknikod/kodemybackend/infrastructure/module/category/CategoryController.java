package pl.sknikod.kodemybackend.infrastructure.module.category;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemybackend.infrastructure.module.category.model.SingleCategoryResponse;
import pl.sknikod.kodemybackend.infrastructure.rest.CategoryControllerDefinition;

@RestController
@AllArgsConstructor
public class CategoryController implements CategoryControllerDefinition {
    private final CategoryUseCase categoryUseCase;

    public ResponseEntity<SingleCategoryResponse> getCategoryDetails(Long categoryId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryUseCase.showCategoryInfo(categoryId));
    }
}
