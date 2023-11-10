package pl.sknikod.kodemybackend.configuration;

import io.vavr.control.Try;
import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.sknikod.kodemybackend.exception.structure.ValidationException;

import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToDateConverter());
    }

    public static class StringToDateConverter implements Converter<String, Date> {
        @Override
        public Date convert(@NonNull String source) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return Try.of(() -> dateFormat.parse(source))
                    .getOrElseThrow(() -> new ValidationException("Uncorrected date format: " + source));
        }
    }


}
