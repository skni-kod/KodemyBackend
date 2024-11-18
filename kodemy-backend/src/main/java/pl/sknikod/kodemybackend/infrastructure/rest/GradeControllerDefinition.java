package pl.sknikod.kodemybackend.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemybackend.infrastructure.module.grade.GradeService;
import pl.sknikod.kodemybackend.infrastructure.module.grade.model.GradeMaterialFilterSearchParams;
import pl.sknikod.kodemybackend.infrastructure.module.grade.model.GradeMaterialSortField;
import pl.sknikod.kodemycommons.doc.SwaggerResponse;

@RequestMapping("")
@SwaggerResponse
@Tag(name = "Grade")
public interface GradeControllerDefinition {
    @Operation(summary = "Add a new grade to the Material")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.BadRequestCode400
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @PostMapping("/api/materials/{materialId}/grades")
    void addGrade(
            @RequestBody @Valid GradeService.MaterialAddGradeRequest body,
            @PathVariable Long materialId);

    @Operation(summary = "Show all Material's grades")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @SwaggerResponse.NotFoundCode404
    @GetMapping("/api/materials/{materialId}/grades")
    ResponseEntity<Page<GradeService.GradePageable>> showGrades(
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page,
            @PathVariable Long materialId,
            @RequestParam(value = "sort", defaultValue = "VALUE") GradeMaterialSortField sort,
            @RequestParam(value = "sort_direction", defaultValue = "DESC") Sort.Direction sortDirection,
            @Parameter(description = "{\"createdDateFrom\":\"2023-01-01T00:00:00\",\"createdDateTo\":\"2023-12-12T23:59:59\"}")
            @RequestParam(value = "filters", required = false) GradeMaterialFilterSearchParams filterSearchParams
    );
}
