package pl.sknikod.kodemy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        SecurityConfig.class,
        H2Config.class
})
public class AppConfig {
}
