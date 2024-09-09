package pl.sknikod.kodemyauth.infrastructure.module.oauth2.configuration;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2RestOperations;

import java.util.Collections;

@Configuration
public class OAuth2ModuleConfig {
    @Bean
    public RestTemplate oAuth2RestTemplate(
            RestTemplateBuilder restTemplateBuilder,
            LogbookClientHttpRequestInterceptor logbookInterceptor
    ) {
        restTemplateBuilder.requestFactory(() -> {
            var requestFactory = new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build());
            return new BufferingClientHttpRequestFactory(requestFactory);
        });
        restTemplateBuilder.additionalInterceptors(Collections.singletonList(logbookInterceptor));
        return restTemplateBuilder.build();
    }

    @Bean
    public OAuth2RestOperations oAuth2RestOperations(RestTemplate oAuth2RestTemplate) {
        return new OAuth2RestOperations(oAuth2RestTemplate);
    }
}
