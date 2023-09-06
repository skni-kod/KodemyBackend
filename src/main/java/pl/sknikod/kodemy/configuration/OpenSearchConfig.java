package pl.sknikod.kodemy.configuration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Configuration
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpenSearchConfig {
    @Bean
    public RestHighLevelClient restHighLevelClient(OpenSearchProperties openSearchProperties) {
        HttpHost[] hosts = openSearchProperties.getHosts().stream().map(HttpHost::create).toArray(HttpHost[]::new);
        return new RestHighLevelClient(RestClient.builder(hosts));
    }

    @Component
    @ConfigurationProperties(prefix = "kodemy.opensearch")
    @Data
    static class OpenSearchProperties {
        private List<String> hosts = new ArrayList<>();
    }
}
