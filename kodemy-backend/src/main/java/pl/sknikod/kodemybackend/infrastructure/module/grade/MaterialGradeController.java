package pl.sknikod.kodemybackend.infrastructure.module.grade;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemybackend.infrastructure.module.grade.model.GradeMaterialSearchFields;
import pl.sknikod.kodemybackend.infrastructure.module.grade.model.GradeMaterialSortField;
import pl.sknikod.kodemybackend.infrastructure.rest.MaterialGradeControllerDefinition;

import java.util.Objects;

@RestController
@AllArgsConstructor
public class MaterialGradeController implements MaterialGradeControllerDefinition {
    private final MaterialGradeUseCase materialGradeUseCase;

    @Override
    @PreAuthorize("isAuthenticated()")
    public void addGrade(MaterialGradeUseCase.MaterialAddGradeRequest body, Long materialId) {
        materialGradeUseCase.addGrade(materialId, body);
    }

    @Override
    public ResponseEntity<Page<MaterialGradeUseCase.GradePageable>> showGrades(int size, int page, Long materialId, GradeMaterialSortField sortField, Sort.Direction sortDirection, GradeMaterialSearchFields searchFields) {
        var pageRequest = PageRequest.of(page, size, sortDirection, sortField.getField());
        var searchFieldsParam = Objects.isNull(searchFields) ? new GradeMaterialSearchFields() : searchFields;
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialGradeUseCase.showGrades(pageRequest, searchFieldsParam, materialId));
    }
}
