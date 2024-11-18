package pl.sknikod.kodemyauth.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2RestTemplate;

import java.util.Collections;

@Configuration
@Slf4j
public class WebConfiguration {

    @Bean
    public BufferingClientHttpRequestFactory bufferingClientHttpRequestFactory() {
        var requestFactory = new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build());
        return new BufferingClientHttpRequestFactory(requestFactory);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(
            RestTemplateBuilder restTemplateBuilder,
            LogbookClientHttpRequestInterceptor logbookInterceptor
    ) {
        restTemplateBuilder.requestFactory(this::bufferingClientHttpRequestFactory);
        restTemplateBuilder.additionalInterceptors(Collections.singletonList(logbookInterceptor));
        return restTemplateBuilder.build();
    }

    @Bean
    public OAuth2RestTemplate oAuth2RestTemplate(
            RestTemplateBuilder restTemplateBuilder,
            LogbookClientHttpRequestInterceptor logbookInterceptor
    ) {
        restTemplateBuilder.requestFactory(this::bufferingClientHttpRequestFactory);
        restTemplateBuilder.additionalInterceptors(Collections.singletonList(logbookInterceptor));
        return new OAuth2RestTemplate(restTemplateBuilder.build());
    }
}
