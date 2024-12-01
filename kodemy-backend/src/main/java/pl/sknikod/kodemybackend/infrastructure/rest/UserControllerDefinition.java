package pl.sknikod.kodemybackend.infrastructure.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Try;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialPageable;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialSortField;
import pl.sknikod.kodemycommons.doc.SwaggerResponse;
import pl.sknikod.kodemycommons.exception.Validation400Exception;

import java.util.List;

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
            @RequestParam(value = "sort", defaultValue = "ID") MaterialSortField sortField,
            @RequestParam(value = "sort_direction", defaultValue = "ASC") Sort.Direction sortDirection,
            @Parameter(description = FilterSearchParams.SEARCH_FIELDS_DOC)
            @RequestParam(value = "filters", required = false) FilterSearchParams filterSearchParams
    );

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    class FilterSearchParams {
        public static final String SEARCH_FIELDS_DOC = """
                {
                    "phrase": "phrase",
                    "id": 1,
                    "statuses": ["PENDING"],
                    "sectionId": 1,
                    "categoryIds": [1,1],
                    "tagIds": [1],
                    "minAvgGrade": 2.2,
                    "maxAvgGrade": 4.2
                }""";

        String phrase;
        Long id;
        List<Material.MaterialStatus> statuses;
        Long sectionId;
        List<Long> categoryIds;
        List<Long> tagIds;
        Double minAvgGrade;
        Double maxAvgGrade;

        @Component
        @RequiredArgsConstructor
        public static class MaterialFilterSearchParamsConverter implements Converter<String, FilterSearchParams> {
            private final ObjectMapper objectMapper;

            @Override
            public FilterSearchParams convert(@NonNull String source) {
                return Try.of(() -> objectMapper.readValue(source, FilterSearchParams.class))
                        .getOrElseThrow(() -> new Validation400Exception("Can't parse " + getClass().getSimpleName() + " params: " + source));
            }
        }
    }
}
