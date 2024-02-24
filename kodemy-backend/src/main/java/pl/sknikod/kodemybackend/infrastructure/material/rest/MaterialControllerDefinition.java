package pl.sknikod.kodemybackend.infrastructure.material.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.material.MaterialCreateUseCase;
import pl.sknikod.kodemybackend.infrastructure.material.MaterialOSearchUseCase;
import pl.sknikod.kodemybackend.infrastructure.material.MaterialUpdateUseCase;
import pl.sknikod.kodemybackend.util.SwaggerResponse;

import javax.validation.Valid;
import java.util.Date;

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
    ResponseEntity<MaterialCreateUseCase.MaterialCreateResponse> create(@RequestBody @Valid MaterialCreateUseCase.MaterialCreateRequest body);

    @Operation(summary = "Update material")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.BadRequestCode400
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @SwaggerResponse.NotFoundCode404
    @PutMapping("/{materialId}")
    ResponseEntity<MaterialUpdateUseCase.MaterialUpdateResponse> update(@PathVariable Long materialId, @RequestBody @Valid MaterialUpdateUseCase.MaterialUpdateRequest body);

    @Operation(summary = "Reindex material")
    @SwaggerResponse.AcceptedCode202
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @PatchMapping("/reindex")
    ResponseEntity<MaterialOSearchUseCase.ReindexResult> reindex(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "Format: yyyy-MM-ddTHH:mm:ss")
            @RequestParam(value = "from") Date from,
            @Parameter(description = "Format: yyyy-MM-ddTHH:mm:ss")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "to") Date to
    );

    @Operation(summary = "Change material's status")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @SwaggerResponse.NotFoundCode404
    @PatchMapping("/{materialId}/status")
    ResponseEntity<SingleMaterialResponse> changeStatus(@PathVariable Long materialId, @RequestBody Material.MaterialStatus status);

    @Operation(summary = "Show material details")
    @SwaggerResponse.SuccessCode200
    @GetMapping("/{materialId}")
    ResponseEntity<SingleMaterialResponse> showDetails(@PathVariable Long materialId);

    @Operation(summary = "Get all materials for admin (including not public)")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @GetMapping("/manage")
    ResponseEntity<Page<SingleMaterialResponse>> manage(
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "sort", defaultValue = "createdDate") String sort,
            @RequestParam(value = "sort_direction", defaultValue = "DESC") Sort.Direction sortDirection,
            @Parameter(description = "{\"phrase\":\"phrase\",\"id\":0,\"title\":\"title\",\"status\":\"PENDING\",\"createdBy\":\"createdBy\",\"createdDateFrom\":\"2023-01-01T00:00:00\",\"createdDateTo\":\"2023-12-12T23:59:59\",\"sectionId\":0,\"categoryId\":0,\"technologyIds\":[0]}")
            @RequestParam(value = "search_fields", required = false) SearchFields searchFields
    );
}
