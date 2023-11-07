package pl.sknikod.kodemysearch.infrastructure.search.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemysearch.exception.structure.ValidationException;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class SearchFields {
    String phrase;
    Long id;
    String title;
    String status;
    SingleMaterialResponse.UserResponse creator;
    Date createdDateFrom;
    Date createdDateTo;
    Long sectionId;
    Long categoryId;
    List<Long> technologyIds;

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
