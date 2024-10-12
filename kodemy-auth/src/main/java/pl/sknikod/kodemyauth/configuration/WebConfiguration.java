package pl.sknikod.kodemyauth.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class WebConfiguration {
    @Getter
    @Setter
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "network.databus")
    public static class LanNetworkProperties {
        private int connectTimeoutMs;
        private int readTimeoutMs;
    }
}
