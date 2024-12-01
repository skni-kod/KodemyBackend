package pl.sknikod.kodemybackend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import pl.sknikod.kodemycommons.exception.handler.RestExceptionHandler;
import pl.sknikod.kodemycommons.network.LanRestTemplate;
import pl.sknikod.kodemycommons.security.JwtProvider;

@Configuration
public class WebConfiguration {

    @Bean
    public RestExceptionHandler restExceptionHandler() {
        return new RestExceptionHandler();
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        return restTemplate;
    }

    @Bean
    public LanRestTemplate lanRestTemplate(
            @Value("${service.connect-timeout-ms}") int connectTimeoutMs, @Value("${service.read-timeout-ms}") int readTimeoutMs, JwtProvider jwtProvider
    ) {
        return new LanRestTemplate(connectTimeoutMs, readTimeoutMs, jwtProvider);
    }
}
