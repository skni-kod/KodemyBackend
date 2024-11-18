package pl.sknikod.kodemyauth.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2RestTemplate;

import java.util.Collections;

@Configuration
@Slf4j
public class WebConfiguration {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(
            RestTemplateBuilder restTemplateBuilder, LogbookClientHttpRequestInterceptor logbookInterceptor
    ) {
        restTemplateBuilder.requestFactory(() -> {
            var requestFactory = new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build());
            return new BufferingClientHttpRequestFactory(requestFactory);
        });
        restTemplateBuilder.additionalInterceptors(Collections.singletonList(logbookInterceptor));
        return restTemplateBuilder.build();
    }
}
