package pl.sknikod.kodemy.infrastructure.material.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemy.infrastructure.search.SearchService;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;
import pl.sknikod.kodemy.infrastructure.search.rest.SearchFields;
import pl.sknikod.kodemy.util.SwaggerResponse;

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
    ResponseEntity<MaterialCreateResponse> create(@RequestBody @Valid MaterialCreateRequest body);

    @Operation(summary = "Update material")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.BadRequestCode400
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @SwaggerResponse.NotFoundCode404
    @PutMapping("/{materialId}")
    ResponseEntity<MaterialUpdateResponse> update(@PathVariable Long materialId, @RequestBody @Valid MaterialUpdateRequest body);

    @Operation(summary = "Reindex material")
    @SwaggerResponse.AcceptedCode202
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @PatchMapping("/reindex")
    ResponseEntity<SearchService.ReindexResult> reindex(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "Format: yyyy-MM-ddTHH:mm:ss")
            @RequestParam(value = "from") Date from,
            @Parameter(description = "Format: yyyy-MM-ddTHH:mm:ss")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "to") Date to
    );

    @Operation(summary = "Show material details")
    @SwaggerResponse.SuccessCode200
    @GetMapping("/{materialId}")
    ResponseEntity<SingleMaterialResponse> showDetails(@PathVariable Long materialId);

    @Operation(summary = "Show all materials")
    @SwaggerResponse.SuccessCode200
    @GetMapping
    ResponseEntity<Page<MaterialSearchObject>> search(
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "sort", defaultValue = "createdDate") String sort,
            @RequestParam(value = "sort_direction", defaultValue = "DESC") Sort.Direction sortDirection,
            @RequestParam(value = "search_fields", required = false) SearchFields searchFields
    );
}
