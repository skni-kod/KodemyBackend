package pl.sknikod.kodemy.infrastructure.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.infrastructure.rest.model.request.MaterialAddGradeRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.request.MaterialCreateRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.response.MaterialCreateResponse;
import pl.sknikod.kodemy.infrastructure.rest.model.response.SingleGradeResponse;

import java.net.URI;
import java.util.Set;

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
    public void addGrade(MaterialAddGradeRequest body, Long materialId) {
        materialService.addGrade(body, materialId);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Set<SingleGradeResponse>> showGrades(Long materialId) {
        return ResponseEntity.ok().body(materialService.showGrades(materialId));
    }
}