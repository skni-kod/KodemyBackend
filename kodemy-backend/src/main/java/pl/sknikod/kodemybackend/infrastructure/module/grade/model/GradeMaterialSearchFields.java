package pl.sknikod.kodemybackend.infrastructure.module.grade.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.SearchFields;
import pl.sknikod.kodemycommon.exception.Validation400Exception;

import java.util.Date;

@Data
@NoArgsConstructor
public class GradeMaterialSearchFields {
    Date createdDateFrom;
    Date createdDateTo;

    @Component
    @RequiredArgsConstructor
    public static class MaterialSearchFieldsConverter implements Converter<String, SearchFields> {
        private final ObjectMapper objectMapper;

        @Override
        public SearchFields convert(@NonNull String source) {
            return Try.of(() -> objectMapper.readValue(source, SearchFields.class))
                    .getOrElseThrow(() -> new Validation400Exception("Can't parse " + getClass().getSimpleName() + " params: " + source));
        }
    }
}