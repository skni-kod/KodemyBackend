package pl.sknikod.kodemysearch.configuration;

import io.jsonwebtoken.lang.Assert;
import io.vavr.control.Try;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jsonb.JsonbJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.*;
import org.opensearch.client.transport.endpoints.BooleanResponse;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemysearch.exception.ExceptionUtil;
import pl.sknikod.kodemysearch.util.opensearch.OpenSearchFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration
@Slf4j
public class OpenSearchConfig {
    @Bean
    public OpenSearchClient openSearchClient(OpenSearchProperties openSearchProperties) throws IOException {
        final var httpHost = Optional.of(openSearchProperties.host).map(HttpHost::create).get();
        final var restClient = RestClient.builder(httpHost).build();/*) {*/
        final var transport = new RestClientTransport(restClient, new JsonbJsonpMapper());
        var client = new OpenSearchClient(transport);
        OpenSearchFactory.of(client).initialize(openSearchProperties.indices);
        return client;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Component
    @ConfigurationProperties(prefix = "opensearch")
    public static class OpenSearchProperties {
        private String host;
        private Map<String, IndexDetails> indices = new HashMap<>();

        @Getter
        @Setter
        @NoArgsConstructor
        public static class IndexDetails {
            private String name;
            private String alias;
        }
    }
}
