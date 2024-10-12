package pl.sknikod.kodemygateway.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class WebConfiguration {

    @Bean
    public CorsWebFilter corsFilter(CorsProperties corsProperties) {
        log.info("Configuring CORS: {}", corsProperties.allowedOrigins);
        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(corsProperties.credentials);
        corsConfiguration.setAllowedOrigins(corsProperties.allowedOrigins);
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(corsProperties.mapping, corsConfiguration);
        return new CorsWebFilter(source);
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
