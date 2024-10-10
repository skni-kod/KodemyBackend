package pl.sknikod.kodemybackend.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialCreateService;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.SingleMaterialResponse;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialUpdateService;
import pl.sknikod.kodemycommons.doc.SwaggerResponse;

import java.time.Instant;

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
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    ResponseEntity<MaterialCreateService.MaterialCreateResponse> create(
            @RequestBody @Valid MaterialCreateService.MaterialCreateRequest body);

    @Operation(summary = "Update material")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.BadRequestCode400
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @SwaggerResponse.NotFoundCode404
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{materialId}")
    ResponseEntity<MaterialUpdateService.MaterialUpdateResponse> update(
            @PathVariable Long materialId, @RequestBody @Valid MaterialUpdateService.MaterialUpdateRequest body);

    @Operation(summary = "Update material status")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.BadRequestCode400
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @SwaggerResponse.NotFoundCode404
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{materialId}/status")
    ResponseEntity<Material.MaterialStatus> updateStatus(
            @PathVariable Long materialId, @RequestParam Material.MaterialStatus newStatus);

    @Operation(summary = "Reindex material")
    @SwaggerResponse.AcceptedCode202
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @PreAuthorize("isAuthenticated() and hasAuthority('CAN_INDEX')")
    @PatchMapping("/reindex")
    ResponseEntity<Void> reindex(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "Format: yyyy-MM-ddTHH:mm:ssZ")
            @RequestParam(value = "from") Instant from,
            @Parameter(description = "Format: yyyy-MM-ddTHH:mm:ssZ")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "to") Instant to
    );

    @Operation(summary = "Show material details")
    @SwaggerResponse.SuccessCode200
    @GetMapping("/{materialId}")
    ResponseEntity<SingleMaterialResponse> showDetails(@PathVariable Long materialId);
}
