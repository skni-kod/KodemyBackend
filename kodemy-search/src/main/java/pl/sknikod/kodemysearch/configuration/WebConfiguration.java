package pl.sknikod.kodemysearch.configuration;

import io.vavr.control.Try;
import jakarta.validation.ValidationException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
public class WebConfiguration {
    @Bean
    public WebMvcConfigurer webSecurityConfigurer(CorsProperties corsProperties) {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping(corsProperties.mapping)
                        .allowedOrigins(corsProperties.allowedOrigins.toArray(String[]::new))
                        .allowCredentials(corsProperties.credentials);
            }

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

    @Getter
    @Setter
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "app.security.cors")
    public static class CorsProperties {
        private List<String> allowedOrigins = new ArrayList<>();
        private String mapping = "/**";
        private boolean credentials = true;
    }
}
