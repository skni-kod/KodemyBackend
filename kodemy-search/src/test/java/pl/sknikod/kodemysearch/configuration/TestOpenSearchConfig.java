package pl.sknikod.kodemysearch.configuration;

import org.mockito.Mockito;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestOpenSearchConfig {
    @Bean
    public OpenSearchClient openSearchClient() {
        return Mockito.mock(OpenSearchClient.class);
    }
}
