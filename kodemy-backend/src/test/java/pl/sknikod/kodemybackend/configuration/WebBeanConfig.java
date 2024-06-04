package pl.sknikod.kodemybackend.configuration;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.sknikod.kodemybackend.infrastructure.common.lan.LanNetworkHandler;

@TestConfiguration
public class WebBeanConfig {
    @Bean
    public WebMvcConfigurer webSecurityConfigurer() {
        return Mockito.mock(WebMvcConfigurer.class);
    }

    @Bean
    public RestTemplate restTemplate() {
        return Mockito.mock(RestTemplate.class);
    }

    @Bean
    public LanNetworkHandler lanNetworkHandler() {
        return Mockito.mock(LanNetworkHandler.class);
    }
}