package pl.sknikod.kodemybackend.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.module.material.add.MaterialCreateUseCase;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialPageable;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialSortField;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.SearchFields;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.SingleMaterialResponse;
import pl.sknikod.kodemybackend.infrastructure.module.material.update.MaterialUpdateUseCase;
import pl.sknikod.kodemycommon.doc.SwaggerResponse;

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
    ResponseEntity<MaterialCreateUseCase.MaterialCreateResponse> create(
            @RequestBody @Valid MaterialCreateUseCase.MaterialCreateRequest body);

    @Operation(summary = "Update material")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.BadRequestCode400
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @SwaggerResponse.NotFoundCode404
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{materialId}")
    ResponseEntity<MaterialUpdateUseCase.MaterialUpdateResponse> update(
            @PathVariable Long materialId, @RequestBody @Valid MaterialUpdateUseCase.MaterialUpdateRequest body);

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

    @Operation(summary = "Get all materials by user (including not public) / manage")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @GetMapping
    @PreAuthorize("isAuthenticated() and hasAuthority('CAN_VIEW_ALL_MATERIALS')")
    ResponseEntity<Page<MaterialPageable>> materialsManage(
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "sort", defaultValue = "CREATED_DATE") MaterialSortField sortField,
            @RequestParam(value = "sort_direction", defaultValue = "DESC") Sort.Direction sortDirection,
            @Parameter(description = SearchFields.SEARCH_FIELDS_DOC)
            @RequestParam(value = "search_fields", required = false) SearchFields searchFields
    );

    @Operation(summary = "Get all materials by user (including not public) / manage")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @GetMapping("/personal")
    ResponseEntity<Page<MaterialPageable>> materialsPersonal(
            @RequestParam(required = false) Long authorId,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "sort", defaultValue = "CREATED_DATE") MaterialSortField sortField,
            @RequestParam(value = "sort_direction", defaultValue = "DESC") Sort.Direction sortDirection,
            @Parameter(description = SearchFields.SEARCH_FIELDS_DOC)
            @RequestParam(value = "search_fields", required = false) SearchFields searchFields
    );
}
