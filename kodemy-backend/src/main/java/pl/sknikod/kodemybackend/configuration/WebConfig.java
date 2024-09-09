package pl.sknikod.kodemybackend.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.sknikod.kodemybackend.infrastructure.common.lan.LanNetworkHandler;
import pl.sknikod.kodemycommon.network.LanRestTemplate;
import pl.sknikod.kodemycommon.security.JwtProvider;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer webSecurityConfigurer(CorsProperties corsProperties) {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping(corsProperties.mapping)
                        .allowedOrigins(corsProperties.allowedOrigins.toArray(String[]::new))
                        .allowCredentials(corsProperties.credentials);
            }
        };
    }

    @Getter
    @Setter
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "app.security.cors")
    public static class CorsProperties {
        private List<String> allowedOrigins = new ArrayList<>();
        private String mapping = "/**";
        private boolean credentials = true;
    }

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
