package pl.sknikod.kodemy.infrastructure.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.infrastructure.rest.model.response.SingleMaterialResponse;

import java.util.Set;

@RestController
@AllArgsConstructor
public class CategoryController implements CategoryControllerDefinition {
    private final CategoryService categoryService;

    @Override
    public ResponseEntity<Set<SingleMaterialResponse>> showMaterials(Long categoryId){
        return ResponseEntity.ok().body(categoryService.showMaterials(categoryId));
    }

}
