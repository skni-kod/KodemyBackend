package pl.sknikod.kodemybackend.infrastructure.material.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemybackend.infrastructure.material.MaterialService;

import java.util.Date;

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
    public ResponseEntity<Page<SingleGradeResponse>> showGrades(Long materialId, Date from, Date to, int size, int page) {
        return ResponseEntity.status(HttpStatus.OK).body(materialService.showGrades(materialId, from, to, page, size));
    }

}
