package pl.sknikod.kodemy.infrastructure.material.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.infrastructure.material.MaterialService;

import java.util.Set;

@RestController
@AllArgsConstructor
public class MaterialGradeController implements MaterialGradeControllerDefinition {
    private final MaterialService materialService;

    @Override
    @PreAuthorize("isAuthenticated()")
    public void addGrade(MaterialAddGradeRequest body, Long materialId) {
        materialService.addGrade(materialId, body);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Set<SingleGradeResponse>> showGrades(Long materialId) {
        return ResponseEntity.status(HttpStatus.OK).body(materialService.showGrades(materialId));
    }
}
