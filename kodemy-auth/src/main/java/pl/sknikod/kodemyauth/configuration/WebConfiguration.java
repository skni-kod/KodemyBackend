package pl.sknikod.kodemyauth.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
