package pl.sknikod.kodemybackend.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialPageable;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialSortField;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.FilterSearchParams;
import pl.sknikod.kodemycommons.doc.SwaggerResponse;

@RequestMapping("/api/users")
@SwaggerResponse
@Tag(name = "User")
public interface UserControllerDefinition {
    @Operation(summary = "Get all materials by user (including not public) / manage")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @GetMapping("/{userId}/materials")
    ResponseEntity<Page<MaterialPageable>> usersMaterials(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "sort", defaultValue = "CREATED_DATE") MaterialSortField sortField,
            @RequestParam(value = "sort_direction", defaultValue = "DESC") Sort.Direction sortDirection,
            @Parameter(description = FilterSearchParams.SEARCH_FIELDS_DOC)
            @RequestParam(value = "filters", required = false) FilterSearchParams filterSearchParams
    );
}
