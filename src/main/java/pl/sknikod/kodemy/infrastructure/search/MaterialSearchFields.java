package pl.sknikod.kodemy.infrastructure.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.exception.structure.ValidationException;
import pl.sknikod.kodemy.infrastructure.common.entity.MaterialStatus;

import java.util.Date;

@Data
@Builder
public class MaterialSearchFields {
    Long id;
    String title;
    MaterialStatus status;
    String createdBy;
    Date createdDate;
    Long categoryId;
    String categoryName;
    String sectionName;

    @Component
    @RequiredArgsConstructor
    public static class MaterialSearchFieldsConverter implements Converter<String, MaterialSearchFields> {
        private final ObjectMapper objectMapper;

        @Override
        public MaterialSearchFields convert(@NonNull String source) {
            return Try.of(() -> objectMapper.readValue(source, MaterialSearchFields.class))
                    .getOrElseThrow(() -> new ValidationException("Can't parse MaterialSearchFields params: " + source));
        }
    }
}
