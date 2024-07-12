package pl.sknikod.kodemyauth.infrastructure.module.user.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.exception.structure.ValidationException;
import pl.sknikod.kodemyauth.infrastructure.database.entity.Role;

@Data
@NoArgsConstructor
public class SearchFields {
    String username;
    String email;
    Role.RoleName role;

    @Component
    @RequiredArgsConstructor
    public static class UserSearchFieldsConverter implements Converter<String, SearchFields> {
        private final ObjectMapper objectMapper;

        @Override
        public SearchFields convert(@NonNull String source) {
            return Try.of(() -> objectMapper.readValue(source, SearchFields.class))
                    .getOrElseThrow(() -> new ValidationException("Can't parse " + getClass().getSimpleName() + " params: " + source));
        }
    }
}

