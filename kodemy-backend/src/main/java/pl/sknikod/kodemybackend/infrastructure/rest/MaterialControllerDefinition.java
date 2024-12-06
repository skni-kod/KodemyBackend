package pl.sknikod.kodemybackend.infrastructure.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Try;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialCreateService;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialUpdateService;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialPageable;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.SingleMaterialResponse;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.StatusesToChangeResponse;
import pl.sknikod.kodemycommons.doc.SwaggerResponse;

import java.time.Instant;
import java.util.List;

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
            @Parameter(description = "Format: yyyy-MM-ddTHH:mm:ssZ", example = "2024-01-01T00:00:00Z")
            @RequestParam(value = "from") Instant from,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "Format: yyyy-MM-ddTHH:mm:ssZ", example = "2024-01-01T00:00:00Z")
            @RequestParam(value = "to") Instant to
    );

    @Operation(summary = "Show material details")
    @SwaggerResponse.SuccessCode200
    @GetMapping("/{materialId}")
    ResponseEntity<SingleMaterialResponse> showDetails(@PathVariable Long materialId);

    @Operation(summary = "Show all possible statuses to change")
    @SwaggerResponse.SuccessCode200
    @GetMapping("/{materialId}/status/evaluate")
    ResponseEntity<StatusesToChangeResponse> showStatusesToChange(@PathVariable Long materialId);

    @Operation(summary = "Show all materials")
    @SwaggerResponse.SuccessCode200
    @SwaggerResponse.UnauthorizedCode401
    @SwaggerResponse.ForbiddenCode403
    @PreAuthorize("isAuthenticated() and hasAuthority('CAN_APPROVED_MATERIAL')")
    @GetMapping("/manage")
    ResponseEntity<Page<MaterialPageable>> manage(
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "sortField", defaultValue = "CREATED_DATE")
            MaterialSortField sortField,
            @RequestParam(value = "sort_direction", defaultValue = "DESC") Sort.Direction sortDirection,
            @Parameter(description = MaterialFilterSearchParams.SEARCH_FIELDS_DOC)
            @RequestParam(value = "filters", required = false) MaterialFilterSearchParams filterSearchParams
    );

    @Getter
    @RequiredArgsConstructor
    public enum MaterialSortField {
        ID("id"),
        TITLE("title"),
        AVG_GRADE("avgGrade"),
        CREATED_DATE("createdDate"),
        SECTION_ID("sectionId"),
        CATEGORY_ID("categoryId");

        private final String field;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    class MaterialFilterSearchParams {
        public static final String SEARCH_FIELDS_DOC = """
                {
                  "phrase": "phrase",
                  "id": 1,
                  "sectionId": 1,
                  "categoryIds": [1],
                  "status": "PENDING"
                }""";

        String phrase;
        Long id;
        Long sectionId;
        List<Long> categoryIds;
        Material.MaterialStatus status;

        @Component
        @RequiredArgsConstructor
        public static class MaterialFilterSearchParamsConverter implements Converter<String, MaterialFilterSearchParams> {
            private final ObjectMapper objectMapper;

            @Override
            public MaterialFilterSearchParams convert(@NonNull String source) {
                return Try.of(() -> objectMapper.readValue(source, MaterialFilterSearchParams.class))
                        .getOrElseThrow(() -> new ValidationException("Can't parse " + getClass().getSimpleName() + " params: " + source));
            }
        }
    }
}
