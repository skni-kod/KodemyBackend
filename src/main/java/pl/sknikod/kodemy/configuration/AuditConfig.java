package pl.sknikod.kodemy.configuration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import pl.sknikod.kodemy.util.EntityAuditorAware;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditConfig {
    @Bean
    public org.springframework.data.domain.AuditorAware<String> auditorAware() {
        return new EntityAuditorAware();
    }
}

