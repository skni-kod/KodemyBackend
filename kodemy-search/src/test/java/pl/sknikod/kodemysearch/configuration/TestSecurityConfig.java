package pl.sknikod.kodemysearch.configuration;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import pl.sknikod.kodemysearch.util.auth.JwtAuthorizationFilter;
import pl.sknikod.kodemysearch.util.auth.JwtService;
import pl.sknikod.kodemysearch.util.auth.handler.AccessControlExceptionHandler;

@TestConfiguration
public class TestSecurityConfig {
    @Bean
    public AccessControlExceptionHandler accessControlExceptionHandler() {
        return Mockito.mock(AccessControlExceptionHandler.class);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(
    ) {
        return Mockito.mock(JwtAuthorizationFilter.class);
    }

    @Bean
    public JwtService jwtService(){
        return Mockito.mock(JwtService.class);
    }
}
