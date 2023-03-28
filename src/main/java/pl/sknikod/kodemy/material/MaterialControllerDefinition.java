package pl.sknikod.kodemy.material;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemy.dto.MaterialCreateRequest;
import pl.sknikod.kodemy.dto.MaterialCreateResponse;
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
}
