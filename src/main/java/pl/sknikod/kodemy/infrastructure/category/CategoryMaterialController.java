package pl.sknikod.kodemy.infrastructure.category;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialOpenSearch;

import java.util.List;

@RestController
@AllArgsConstructor
public class CategoryMaterialController implements CategoryMaterialControllerDefinition {
    private final CategoryService categoryService;

    public ResponseEntity<List<MaterialOpenSearch>> getMaterialsByCategory(Long categoryId, Integer limit) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.showMaterials(categoryId, limit));
    }

}
