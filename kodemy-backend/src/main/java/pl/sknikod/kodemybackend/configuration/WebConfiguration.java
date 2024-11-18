package pl.sknikod.kodemybackend.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.sknikod.kodemycommons.exception.handler.RestExceptionHandler;
import pl.sknikod.kodemycommons.network.LanRestTemplate;
import pl.sknikod.kodemycommons.security.JwtProvider;

@Configuration
public class WebConfiguration {

    @Bean
    public RestExceptionHandler restExceptionHandler(){
        return new RestExceptionHandler();
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        return restTemplate;
    }

    @Bean
    public LanRestTemplate lanRestTemplate(LanNetworkProperties lanNetworkProperties, JwtProvider jwtProvider) {
        return new LanRestTemplate(lanNetworkProperties.connectTimeoutMs, lanNetworkProperties.readTimeoutMs, jwtProvider);
    }

    @Getter
    @Setter
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "network.lan")
    public static class LanNetworkProperties {
        private String username;
        private String password;
        private int connectTimeoutMs;
        private int readTimeoutMs;
    }
}
