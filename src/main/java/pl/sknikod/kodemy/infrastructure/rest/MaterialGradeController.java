package pl.sknikod.kodemy.infrastructure.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialAddGradeRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.SingleGradeResponse;

import java.util.Set;

@RestController
@AllArgsConstructor
public class MaterialGradeController implements MaterialGradeControllerDefinition {
    private final MaterialService materialService;

    @Override
    @PreAuthorize("isAuthenticated()")
    public void addGrade(MaterialAddGradeRequest body, Long materialId) {
        materialService.addGrade(body, materialId);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Set<SingleGradeResponse>> showGrades(Long materialId) {
        return ResponseEntity.status(HttpStatus.OK).body(materialService.showGrades(materialId));
    }
}
