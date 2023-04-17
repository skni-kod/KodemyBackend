package pl.sknikod.kodemy.material;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.rest.request.MaterialAddGradeRequest;
import pl.sknikod.kodemy.rest.request.MaterialCreateRequest;
import pl.sknikod.kodemy.rest.response.MaterialCreateResponse;
import pl.sknikod.kodemy.rest.response.MaterialShowGradesResponse;

import java.net.URI;

@RestController
@AllArgsConstructor
public class MaterialController implements MaterialControllerDefinition {
    private final MaterialService materialService;

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MaterialCreateResponse> create(MaterialCreateRequest body) {
        var materialResponse = materialService.create(body);
        return ResponseEntity
                .created(URI.create("/api/materials/" + materialResponse.getId()))
                .body(materialResponse);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void addGrade(MaterialAddGradeRequest body, @PathVariable Long materialId) {
        materialService.addGrade(body, materialId);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MaterialShowGradesResponse> showGrades(@PathVariable Long materialId) {
        var gradesResponse = materialService.showGrades(materialId);
        return ResponseEntity.ok().body(gradesResponse);
    }
}