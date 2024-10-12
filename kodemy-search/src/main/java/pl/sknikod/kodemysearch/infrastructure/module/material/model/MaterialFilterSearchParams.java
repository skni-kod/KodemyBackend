package pl.sknikod.kodemysearch.infrastructure.module.material.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import jakarta.validation.ValidationException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MaterialFilterSearchParams {
    // @formatter:off
        public static final String SEARCH_FIELDS_DOC = """
                {
                  "phrase": "phrase",
                  "id": 0,
                  "title": "title",
                  "status": "PENDING",
                  "createdBy": "createdBy",
                  "createdDateFrom": "2023-01-01T00:00:00",
                  "createdDateTo": "2023-12-12T23:59:59",
                  "sectionId": 0,
                  "minAvgGrade": 2.2,
                  "maxAvgGrade": 4.2,
                  "categoryId": 0,
                  "tagIds": [0]
                }""";
        // @formatter:on

    String phrase;
    Long id;
    String title;
    String status;
    String createdBy;
    LocalDateTime createdDateFrom;
    LocalDateTime createdDateTo;
    Long sectionId;
    Long[] categoryIds;
    Long[] tagIds;
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