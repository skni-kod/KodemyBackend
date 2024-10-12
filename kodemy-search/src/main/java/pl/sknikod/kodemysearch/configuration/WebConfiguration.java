package pl.sknikod.kodemysearch.configuration;

import io.vavr.control.Try;
import jakarta.validation.ValidationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
public class WebConfiguration {
    @Bean
    public WebMvcConfigurer webSecurityConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addFormatters(@NonNull FormatterRegistry registry) {
                registry.addConverter(new Converter<String, Date>() {
                    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                    @Override
                    public Date convert(@NonNull String source) {
                        return Try.of(() -> DATE_FORMAT.parse(source))
                                .getOrElseThrow(() -> new ValidationException("Uncorrected date format: " + source));
                    }
                });
            }
        };
    }
}
