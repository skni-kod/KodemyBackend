package pl.sknikod.kodemy.configuration;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OpenSearchConfig {
    private final OpenSearchProperties openSearchProperties;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        HttpHost[] hosts = openSearchProperties.getHosts().stream().map(HttpHost::create).toArray(HttpHost[]::new);
        return new RestHighLevelClient(RestClient.builder(hosts));
    }

    @Bean
    public RequestOptions requestOptions() {
        return RequestOptions.DEFAULT;
    }

    @Component
    @ConfigurationProperties(prefix = "opensearch")
    @Data
    static class OpenSearchProperties {
        private List<String> hosts = new ArrayList<>();
    }
}
