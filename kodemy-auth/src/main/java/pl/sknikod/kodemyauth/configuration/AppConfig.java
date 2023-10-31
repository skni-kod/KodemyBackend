package pl.sknikod.kodemyauth.configuration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Optional;

@Configuration
@Import({
        SecurityConfig.class,
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AppConfig {
    @Bean
    public org.springframework.data.domain.AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName);
    }
}
