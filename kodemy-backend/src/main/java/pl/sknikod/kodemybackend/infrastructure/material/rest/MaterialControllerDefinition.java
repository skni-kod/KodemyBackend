package pl.sknikod.kodemybackend.infrastructure.material.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.util.SwaggerResponse;

import javax.validation.Valid;

@RequestMapping("/api/materials")
@SwaggerResponse
@Tag(name = "Material")
public interface MaterialControllerDefinition {
    @Operation(summary = "Create a new material")
    @SwaggerResponse.CreatedCode201
    @SwaggerResponse.BadRequestCode400
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @SwaggerResponse.NotFoundCode404
    @SwaggerResponse.ConflictCode409
    @PostMapping
    ResponseEntity<MaterialCreateResponse> create(@RequestBody @Valid MaterialCreateRequest body);

    @Operation(summary = "Update material")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.BadRequestCode400
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @SwaggerResponse.NotFoundCode404
    @PutMapping("/{materialId}")
    ResponseEntity<MaterialUpdateResponse> update(@PathVariable Long materialId, @RequestBody @Valid MaterialUpdateRequest body);

    @Operation(summary = "Show material details")
    @SwaggerResponse.SuccessCode200
    @GetMapping("/{materialId}")
    ResponseEntity<SingleMaterialResponse> showDetails(@PathVariable Long materialId);

    @Operation(summary = "Change material's status")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @SwaggerResponse.NotFoundCode404
    @PatchMapping("/{materialId}/status")
    ResponseEntity<SingleMaterialResponse> changeStatus(@PathVariable Long materialId, @RequestBody Material.MaterialStatus status);
}
