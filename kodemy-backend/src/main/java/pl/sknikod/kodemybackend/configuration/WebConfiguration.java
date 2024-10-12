package pl.sknikod.kodemybackend.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.sknikod.kodemybackend.infrastructure.common.lan.LanNetworkHandler;
import pl.sknikod.kodemycommons.network.LanRestTemplate;
import pl.sknikod.kodemycommons.security.JwtProvider;

@Configuration
public class WebConfiguration {
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        return restTemplate;
    }

    @Bean
    public LanNetworkHandler lanNetworkHandler(
            LanNetworkProperties lanNetworkProperties,
            JwtProvider jwtProvider,
            @Value("${network.routes.auth}") String authRouteBaseUrl
    ) {
        LanRestTemplate lanRestTemplate = new LanRestTemplate(
                lanNetworkProperties.connectTimeoutMs, lanNetworkProperties.readTimeoutMs, jwtProvider
        );
        return new LanNetworkHandler(lanRestTemplate, authRouteBaseUrl);
    }

    @Getter
    @Setter
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "network.databus")
    public static class LanNetworkProperties {
        private String username;
        private String password;
        private int connectTimeoutMs;
        private int readTimeoutMs;
    }
}
