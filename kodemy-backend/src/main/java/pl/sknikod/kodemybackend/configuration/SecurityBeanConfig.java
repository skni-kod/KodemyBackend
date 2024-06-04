package pl.sknikod.kodemybackend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import pl.sknikod.kodemybackend.util.auth.JwtAuthorizationFilter;
import pl.sknikod.kodemybackend.util.auth.JwtService;
import pl.sknikod.kodemybackend.util.auth.handler.AccessControlExceptionHandler;
import pl.sknikod.kodemybackend.util.data.AuditorAwareAdapter;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SecurityBeanConfig {
    @Bean
    public AuditorAware<?> auditorAware() {
        return new AuditorAwareAdapter();
    }

    @Bean
    public AccessControlExceptionHandler accessControlExceptionHandler(ObjectMapper objectMapper) {
        return new AccessControlExceptionHandler(objectMapper);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(
            JwtService jwtService
    ){
        return new JwtAuthorizationFilter(jwtService);
    }
}
