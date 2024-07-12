package pl.sknikod.kodemybackend.configuration;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import pl.sknikod.kodemybackend.util.auth.JwtAuthorizationFilter;
import pl.sknikod.kodemybackend.util.auth.JwtService;
import pl.sknikod.kodemybackend.util.auth.handler.AccessControlExceptionHandler;
import pl.sknikod.kodemybackend.util.data.AuditorAwareAdapter;

@TestConfiguration
public class TestSecurityConfig {
    @Bean
    public AuditorAware<?> auditorAware() {
        return Mockito.mock(AuditorAwareAdapter.class);
    }

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