package pl.sknikod.kodemy.material;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemy.rest.request.MaterialAddGradeRequest;
import pl.sknikod.kodemy.rest.request.MaterialCreateRequest;
import pl.sknikod.kodemy.rest.response.MaterialCreateResponse;
import pl.sknikod.kodemy.util.SwaggerResponse;

import javax.validation.Valid;

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
}
