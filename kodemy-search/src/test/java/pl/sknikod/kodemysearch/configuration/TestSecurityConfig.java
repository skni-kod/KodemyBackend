package pl.sknikod.kodemysearch.configuration;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import pl.sknikod.kodemycommons.exception.handler.ServletExceptionHandler;
import pl.sknikod.kodemycommons.security.JwtAuthorizationFilter;
import pl.sknikod.kodemycommons.security.JwtProvider;

@TestConfiguration
public class TestSecurityConfig {
    @Bean
    public ServletExceptionHandler servletExceptionHandler() {
        return Mockito.mock(ServletExceptionHandler.class);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(
    ) {
        return Mockito.mock(JwtAuthorizationFilter.class);
    }

    @Bean
    public JwtProvider jwtProvider(){
        return Mockito.mock(JwtProvider.class);
    }
}
