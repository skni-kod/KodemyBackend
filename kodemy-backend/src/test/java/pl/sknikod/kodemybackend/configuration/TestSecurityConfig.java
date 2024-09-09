package pl.sknikod.kodemybackend.configuration;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import pl.sknikod.kodemybackend.util.data.AuditorAwareAdapter;
import pl.sknikod.kodemycommon.exception.handler.ServletExceptionHandler;
import pl.sknikod.kodemycommon.security.JwtAuthorizationFilter;
import pl.sknikod.kodemycommon.security.JwtProvider;

@TestConfiguration
public class TestSecurityConfig {
    @Bean
    public AuditorAware<?> auditorAware() {
        return Mockito.mock(AuditorAwareAdapter.class);
    }

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