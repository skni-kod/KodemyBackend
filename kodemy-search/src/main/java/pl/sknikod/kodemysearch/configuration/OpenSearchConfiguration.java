package pl.sknikod.kodemysearch.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.httpclient.LogbookHttpRequestInterceptor;
import pl.sknikod.kodemysearch.util.opensearch.OpenSearchClientEnhanced;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
@Slf4j
public class OpenSearchConfiguration {
    @Bean
    public OpenSearchClient openSearchClient(
            OpenSearchProperties openSearchProperties,
            Logbook logbook
    ) {
        final var httpHost = Optional.of(openSearchProperties.host)
                .map(HttpHost::create).stream().toArray(HttpHost[]::new);

        final var credentials = new BasicCredentialsProvider();
        credentials.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
                openSearchProperties.username, openSearchProperties.password));

        final var restClient = RestClient.builder(httpHost)
                .setHttpClientConfigCallback(config -> config
                        .setDefaultCredentialsProvider(new BasicCredentialsProvider())
                        .addInterceptorFirst(new LogbookHttpRequestInterceptor(logbook))
                )
                .build();

        var openSearchClient = new OpenSearchClientEnhanced(new RestClientTransport(restClient, new JacksonJsonpMapper()));
        openSearchProperties.indices.values().forEach(openSearchClient::initializeIndex);
        return openSearchClient;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Component
    @ConfigurationProperties(prefix = "opensearch")
    public static class OpenSearchProperties {
        private String host;
        private String username;
        private String password;
        private Map<String, OpenSearchClientEnhanced.Index> indices = new HashMap<>();
    }
}
