package pl.sknikod.kodemyauth.util;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import pl.sknikod.kodemyauth.util.auth.JwtService;

@TestConfiguration
public class UtilBeanConfig {
    @Bean
    public JwtService jwtService(){
        return Mockito.mock(JwtService.class);
    }
}
