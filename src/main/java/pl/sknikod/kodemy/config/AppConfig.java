package pl.sknikod.kodemy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        SecurityConfig.class,
        AuditConfig.class
})
public class AppConfig {
}
