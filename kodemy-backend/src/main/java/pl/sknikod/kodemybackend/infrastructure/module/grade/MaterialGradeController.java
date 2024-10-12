package pl.sknikod.kodemybackend.infrastructure.module.grade;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemybackend.infrastructure.module.grade.model.GradeMaterialFilterSearchParams;
import pl.sknikod.kodemybackend.infrastructure.module.grade.model.GradeMaterialSortField;
import pl.sknikod.kodemybackend.infrastructure.rest.MaterialGradeControllerDefinition;

import java.util.Objects;

@RestController
@AllArgsConstructor
public class MaterialGradeController implements MaterialGradeControllerDefinition {
    private final MaterialGradeService materialGradeService;

    @Override
    @PreAuthorize("isAuthenticated()")
    public void addGrade(MaterialGradeService.MaterialAddGradeRequest body, Long materialId) {
        materialGradeService.addGrade(materialId, body);
    }

    @Override
    public ResponseEntity<Page<MaterialGradeService.GradePageable>> showGrades(int size, int page, Long materialId, GradeMaterialSortField sortField, Sort.Direction sortDirection, GradeMaterialFilterSearchParams filterSearchParams) {
        var pageRequest = PageRequest.of(page, size, sortDirection, sortField.getField());
        var filterSearchParamsParam = Objects.isNull(filterSearchParams) ? new GradeMaterialFilterSearchParams() : filterSearchParams;
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialGradeService.showGrades(pageRequest, filterSearchParamsParam, materialId));
    }
}
