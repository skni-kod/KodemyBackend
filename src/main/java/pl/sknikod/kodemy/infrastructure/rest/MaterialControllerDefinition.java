package pl.sknikod.kodemy.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialAddGradeRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialCreateRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialCreateResponse;
import pl.sknikod.kodemy.infrastructure.rest.model.SingleGradeResponse;
import pl.sknikod.kodemy.util.SwaggerResponse;

import javax.validation.Valid;
import java.util.Set;

@RequestMapping("/api/materials")
@SwaggerResponse
@Tag(name = "Material")
public interface MaterialControllerDefinition {
    @Operation(summary = "Create a new material")
    @SwaggerResponse.AuthRequest
    @SwaggerResponse.CreateRequest
    @PostMapping
    ResponseEntity<MaterialCreateResponse> create(@RequestBody @Valid MaterialCreateRequest body);

    @Operation(summary = "Add a new grade to the Material")
    @SwaggerResponse.AuthRequest
    @SwaggerResponse.CreateRequest
    @PostMapping("/{materialId}/grades")
    void addGrade(@RequestBody @Valid MaterialAddGradeRequest body, @PathVariable Long materialId);

    @Operation(summary = "Show all Material's grades")
    @SwaggerResponse.AuthRequest
    @SwaggerResponse.ReadRequest
    @GetMapping("/{materialId}/grades")
    ResponseEntity<Set<SingleGradeResponse>> showGrades(@PathVariable Long materialId);
}
