package pl.sknikod.kodemyauth.infrastructure.module.user.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemycommons.exception.Validation400Exception;

@Data
@NoArgsConstructor
public class FilterSearchParams {
    String username;
    String email;
    String roleName;

    @Component
    @RequiredArgsConstructor
    public static class UserFilterSearchParamsConverter implements Converter<String, FilterSearchParams> {
        private final ObjectMapper objectMapper;

        @Override
        public FilterSearchParams convert(@NonNull String source) {
            return Try.of(() -> objectMapper.readValue(source, FilterSearchParams.class))
                    .getOrElseThrow(() -> new Validation400Exception("Can't parse " + getClass().getSimpleName() + " params: " + source));
        }
    }
}

