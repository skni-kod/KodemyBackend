package pl.sknikod.kodemy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pl.sknikod.kodemy.util.ExceptionRestGenericMessage;

@Configuration
@Import({
        SecurityConfig.class,
        AuditConfig.class
})
public class AppConfig {
    @Bean
    public ExceptionRestGenericMessage exceptionRestGenericMessage(){
        return new ExceptionRestGenericMessage();
    }
}
