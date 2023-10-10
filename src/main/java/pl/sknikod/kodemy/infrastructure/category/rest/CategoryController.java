package pl.sknikod.kodemy.infrastructure.category.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.infrastructure.category.CategoryService;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;
import pl.sknikod.kodemy.infrastructure.search.rest.SearchFields;

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
