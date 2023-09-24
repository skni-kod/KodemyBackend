package pl.sknikod.kodemy.infrastructure.material.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemy.infrastructure.search.SearchService;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;
import pl.sknikod.kodemy.util.SwaggerResponse;

import javax.validation.Valid;
import java.util.Date;

@RequestMapping("/api/materials")
@SwaggerResponse
@Tag(name = "Material")
public interface MaterialControllerDefinition {
    @Operation(summary = "Create a new material")
    @SwaggerResponse.UnauthorizedCode
    @SwaggerResponse.ForbiddenCode
    @SwaggerResponse.CreatedCode
    @SwaggerResponse.BadRequestCode
    @SwaggerResponse.NotFoundCode
    @PostMapping
    ResponseEntity<MaterialCreateResponse> create(@RequestBody @Valid MaterialCreateRequest body);

    @Operation(summary = "Update material")
    @SwaggerResponse.BadRequestCode
    @SwaggerResponse.UnauthorizedCode
    @SwaggerResponse.ForbiddenCode
    @SwaggerResponse.SuccessCode
    @SwaggerResponse.NotFoundCode
    @PutMapping("/{materialId}")
    ResponseEntity<MaterialUpdateResponse> update(@PathVariable Long materialId, @RequestBody @Valid MaterialUpdateRequest body);

    @Operation(summary = "Reindex material")
    @SwaggerResponse.UnauthorizedCode
    @SwaggerResponse.ForbiddenCode
    @SwaggerResponse.AcceptedCode
    @PatchMapping("/reindex")
    ResponseEntity<SearchService.ReindexResult> reindex(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "from") Date from,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(value = "to") Date to
    );

    @Operation(summary = "Search for material")
    @SwaggerResponse.SuccessCode
    @GetMapping("/{phrase}")
    ResponseEntity<Page<MaterialSearchObject>> search(
            @PathVariable String phrase,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "sort", defaultValue = "createdDate") String sort,
            @RequestParam(value = "sort_direction", defaultValue = "DESC") Sort.Direction sortDirection
    );
}
