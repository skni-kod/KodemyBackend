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
import pl.sknikod.kodemybackend.infrastructure.rest.GradeControllerDefinition;

import java.util.Objects;

@RestController
@AllArgsConstructor
public class GradeController implements GradeControllerDefinition {
    private final GradeService gradeService;

    @Override
    @PreAuthorize("isAuthenticated()")
    public void addGrade(GradeService.MaterialAddGradeRequest body, Long materialId) {
        gradeService.addGrade(materialId, body);
    }

    @Override
    public ResponseEntity<Page<GradeService.GradePageable>> showGrades(
            int size, int page, Long materialId, GradeMaterialSortField sortField,
            Sort.Direction sortDirection, GradeMaterialFilterSearchParams filterSearchParams
    ) {
        var pageRequest = PageRequest.of(page, size, sortDirection, sortField.getField());
        var filterSearchParamsParam = Objects.isNull(filterSearchParams) ? new GradeMaterialFilterSearchParams() : filterSearchParams;
        return ResponseEntity.status(HttpStatus.OK)
                .body(gradeService.showGrades(pageRequest, filterSearchParamsParam, materialId));
    }
}
