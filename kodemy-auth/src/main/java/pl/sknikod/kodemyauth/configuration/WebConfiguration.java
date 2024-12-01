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

import java.util.Collections;

@Configuration
@Slf4j
public class WebConfiguration {
    public static final String OAUTH2_REST_TEMPLATE = "oAuth2RestTemplate";

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(
            RestTemplateBuilder restTemplateBuilder,
            LogbookClientHttpRequestInterceptor logbookInterceptor
    ) {
        var requestFactory = new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build());
        restTemplateBuilder.requestFactory(() -> new BufferingClientHttpRequestFactory(requestFactory));
        restTemplateBuilder.additionalInterceptors(Collections.singletonList(logbookInterceptor));
        return restTemplateBuilder.build();
    }

    @Bean(OAUTH2_REST_TEMPLATE)
    public RestTemplate oAuth2RestTemplate(
            RestTemplateBuilder restTemplateBuilder,
            LogbookClientHttpRequestInterceptor logbookInterceptor
    ) {
        var requestFactory = new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build());
        restTemplateBuilder.requestFactory(() -> new BufferingClientHttpRequestFactory(requestFactory));
        restTemplateBuilder.additionalInterceptors(Collections.singletonList(logbookInterceptor));
        return restTemplateBuilder.build();
    }
}
