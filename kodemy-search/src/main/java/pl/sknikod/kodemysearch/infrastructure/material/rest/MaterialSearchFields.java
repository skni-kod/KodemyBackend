package pl.sknikod.kodemysearch.infrastructure.material.rest;

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
public class MaterialSearchFields {
    String phrase;
    Long id;
    String title;
    String status;
    String createdBy;
    Date createdDateFrom;
    Date createdDateTo;
    Long sectionId;
    Long categoryId;
    List<Long> technologyIds;

    @Component
    @RequiredArgsConstructor
    public static class MaterialSearchFieldsConverter implements Converter<String, MaterialSearchFields> {
        private final ObjectMapper objectMapper;

        @Override
        public MaterialSearchFields convert(@NonNull String source) {
            return Try.of(() -> objectMapper.readValue(source, MaterialSearchFields.class))
                    .getOrElseThrow(() -> new ValidationException("Can't parse " + getClass().getSimpleName() + " params: " + source));
        }
    }
}
