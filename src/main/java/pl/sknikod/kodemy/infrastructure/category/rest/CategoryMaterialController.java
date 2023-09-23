package pl.sknikod.kodemy.infrastructure.category.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.infrastructure.category.CategoryService;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;

@RestController
@AllArgsConstructor
public class CategoryMaterialController implements CategoryMaterialControllerDefinition {
    private final CategoryService categoryService;

    public ResponseEntity<Page<MaterialSearchObject>> getMaterialsByCategory(
            Long categoryId, int size, int page, String sort, Sort.Direction sortDirection
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                categoryService.showMaterials(categoryId, size, page, sort, sortDirection)
        );
    }

    public ResponseEntity<SingleCategoryResponse> getCategoryDetails(Long categoryId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                categoryService.showCategoryInfo(categoryId)
        );
    }
}
