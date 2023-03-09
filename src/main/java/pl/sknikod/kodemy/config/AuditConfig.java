package pl.sknikod.kodemy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import pl.sknikod.kodemy.util.AuditorAware;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AuditConfig {
    @Bean
    public org.springframework.data.domain.AuditorAware<String> auditorAware() {
        return new AuditorAware();
    }
}

