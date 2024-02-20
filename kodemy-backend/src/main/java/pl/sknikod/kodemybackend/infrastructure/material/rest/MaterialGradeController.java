package pl.sknikod.kodemybackend.infrastructure.material.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemybackend.infrastructure.material.MaterialService;

import java.util.Objects;

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
    public ResponseEntity<Page<SingleGradeResponse>> showGrades(int size, int page, String sort, Sort.Direction sortDirection, GradeMaterialSearchFields searchFields) {
        var pageRequest = PageRequest.of(page, size, sortDirection, sort);
        var searchFieldsParam = Objects.isNull(searchFields) ? new GradeMaterialSearchFields() : searchFields;
        return ResponseEntity.status(HttpStatus.OK).body(
                materialService.showGrades(pageRequest, searchFieldsParam)
        );
    }
}
