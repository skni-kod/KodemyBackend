package pl.sknikod.kodemybackend.infrastructure.material.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemybackend.infrastructure.material.MaterialGradeUseCase;
import pl.sknikod.kodemybackend.util.SwaggerResponse;

import javax.validation.Valid;

@RequestMapping("/api/materials")
@SwaggerResponse
@Tag(name = "Material")
public interface MaterialGradeControllerDefinition {
    @Operation(summary = "Add a new grade to the Material")
    @SwaggerResponse.CreatedCode201
    @SwaggerResponse.BadRequestCode400
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @PostMapping("/{materialId}/grades")
    void addGrade(@RequestBody @Valid MaterialGradeUseCase.MaterialAddGradeRequest body, @PathVariable Long materialId);

    @Operation(summary = "Show all Material's grades")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @SwaggerResponse.NotFoundCode404
    @GetMapping("/{materialId}/grades")
    ResponseEntity<Page<MaterialGradeUseCase.GradePageable>> showGrades(
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "sort", defaultValue = "createdDate") String sort,
            @RequestParam(value = "sort_direction", defaultValue = "DESC") Sort.Direction sortDirection,
            @Parameter(description = "{\"materialId\":number, \"createdDateFrom\":\"2023-01-01T00:00:00\",\"createdDateTo\":\"2023-12-12T23:59:59\"}")
            @RequestParam(value = "search_fields", required = false) GradeMaterialSearchFields searchFields
    );
}
