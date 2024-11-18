package pl.sknikod.kodemysearch.configuration;

import io.vavr.control.Try;
import jakarta.validation.ValidationException;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

@Configuration
public class WebConfiguration {
    @Bean
    public WebMvcConfigurer webSecurityConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addFormatters(@NonNull FormatterRegistry registry) {
                registry.addConverter(new Converter<String, Date>() {
                    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                    @Override
                    public Date convert(@NonNull String source) {
                        return Try.of(() -> DATE_FORMAT.parse(source))
                                .getOrElseThrow(() -> new ValidationException("Uncorrected date format: " + source));
                    }
                });
            }
        };
    }

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
