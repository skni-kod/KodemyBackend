package pl.sknikod.kodemy.material;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemy.grade.Grade;
import pl.sknikod.kodemy.rest.request.MaterialAddGradeRequest;
import pl.sknikod.kodemy.rest.request.MaterialCreateRequest;
import pl.sknikod.kodemy.rest.response.MaterialCreateResponse;
import pl.sknikod.kodemy.rest.response.MaterialShowGradesResponse;
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
    @SwaggerResponse.CreateRequest
    @GetMapping("/{materialId}/grades")
    ResponseEntity<MaterialShowGradesResponse> showGrades(@PathVariable Long materialId);
}
