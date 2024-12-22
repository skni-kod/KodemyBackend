package pl.sknikod.kodemyauth.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public BufferingClientHttpRequestFactory clientHttpRequestFactory() {
        final var httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom().build())
                .evictExpiredConnections()
                .build();
        return new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(
            RestTemplateBuilder restTemplateBuilder,
            BufferingClientHttpRequestFactory clientHttpRequestFactory,
            LogbookClientHttpRequestInterceptor logbookInterceptor
    ) {
        return restTemplateBuilder
                .requestFactory(() -> clientHttpRequestFactory)
                .additionalInterceptors(Collections.singletonList(logbookInterceptor))
                .build();
    }

    @Bean(OAUTH2_REST_TEMPLATE)
    public RestTemplate oAuth2RestTemplate(
            RestTemplateBuilder restTemplateBuilder,
            BufferingClientHttpRequestFactory clientHttpRequestFactory,
            LogbookClientHttpRequestInterceptor logbookInterceptor
    ) {
        return restTemplateBuilder
                .requestFactory(() -> clientHttpRequestFactory)
                .additionalInterceptors(Collections.singletonList(logbookInterceptor))
                .build();
    }
}
