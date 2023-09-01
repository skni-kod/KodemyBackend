package pl.sknikod.kodemy.infrastructure.category.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.infrastructure.category.CategoryService;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;

import java.util.List;

@RestController
@AllArgsConstructor
public class CategoryMaterialController implements CategoryMaterialControllerDefinition {
    private final CategoryService categoryService;

    public ResponseEntity<List<MaterialSearchObject>> getMaterialsByCategory(Long categoryId, int size, int page) {
        return ResponseEntity.status(HttpStatus.OK).body(
                categoryService.showMaterials(categoryId, size, page)
        );
    }

}
