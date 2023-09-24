package pl.sknikod.kodemy.infrastructure.search.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.exception.structure.ValidationException;

import java.util.Date;

@Data
@NoArgsConstructor
public class SearchFields {
    String phrase;
    Long id;
    String title;
    String status;
    String createdBy;
    Date createdDateFrom;
    Date createdDateTo;
    Long categoryId;
    String categoryName;
    String sectionName;

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
