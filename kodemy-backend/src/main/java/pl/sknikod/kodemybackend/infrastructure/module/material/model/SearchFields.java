package pl.sknikod.kodemybackend.infrastructure.module.material.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.exception.structure.ValidationException;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Material;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class SearchFields {
    // @formatter:off
    public static final String SEARCH_FIELDS_DOC = """
            {
                "phrase": "phrase",
                "id": 0,
                "statuses": ["PENDING"],
                "createdBy": "createdBy",
                "createdDateFrom": "2023-01-01T00:00:00",
                "createdDateTo": "2023-12-12T23:59:59",
                "sectionId": 0,
                "categoryIds": [0],
                "minAvgGrade": 2.2,
                "maxAvgGrade": 4.2,
                "tagIds": [0],
                "userId": 0,
            }""";
    // @formatter:on

    String phrase;
    Long id;
    List<Material.MaterialStatus> statuses;
    String createdBy;
    LocalDateTime createdDateFrom;
    LocalDateTime createdDateTo;
    Long sectionId;
    List<Long> categoryIds;
    List<Long> tagIds;
    Double minAvgGrade;
    Double maxAvgGrade;
    Long userId;

    @Component
    @RequiredArgsConstructor
    public static class MaterialSearchFieldsConverter implements Converter<String, SearchFields> {
        private final ObjectMapper objectMapper;

        @Override
        public SearchFields convert(@NonNull String source) {
            return Try.of(() -> objectMapper.readValue(source, SearchFields.class))
                    .getOrElseThrow(() -> new ValidationException("Can't parse " + getClass().getSimpleName() + " params: " + source));
        }
    }
}

