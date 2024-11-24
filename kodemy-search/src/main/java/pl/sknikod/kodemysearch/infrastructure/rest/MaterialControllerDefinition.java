package pl.sknikod.kodemysearch.infrastructure.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Try;
import jakarta.validation.ValidationException;
import lombok.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.sknikod.kodemycommons.doc.SwaggerResponse;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialPageable;

import java.util.List;

@RequestMapping("/api/materials")
@SwaggerResponse
@Tag(name = "Material")
public interface MaterialControllerDefinition {
    @Operation(summary = "Show all APPROVED materials")
    @SwaggerResponse.SuccessCode200
    @GetMapping
    ResponseEntity<Page<MaterialPageable>> search(
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
                  "categoryIds": [1,1],
                  "minAvgGrade": 2.2,
                  "maxAvgGrade": 4.2
                }""";

        String phrase;
        Long id;
        Long sectionId;
        List<Long> categoryIds;
        List<Long> tagsIds;
        Double minAvgGrade;
        Double maxAvgGrade;

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
