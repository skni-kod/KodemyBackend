package pl.sknikod.kodemybackend;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@ContextConfiguration(classes = KodemyBackendApplication.class)
@ImportAutoConfiguration(value = TestChannelBinderConfiguration.class)
@Testcontainers
public class SuperclassTest {
    @Container
    private static final PostgreSQLContainer<?> dataSource;
    private static final WireMockServer wireMockServer;

    static {
        dataSource = new PostgreSQLContainer<>("postgres:14.1-alpine");
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    }

    @BeforeAll
    public static void startWireMock() {
        if (wireMockServer.isRunning()) {
            return;
        }
        wireMockServer.start();
    }

    @AfterAll
    public static void stopWireMock() {
        if (!wireMockServer.isRunning()) {
            return;
        }
        wireMockServer.stop();
    }

    @DynamicPropertySource
    private static void containerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", dataSource::getJdbcUrl);
        registry.add("spring.datasource.username", dataSource::getUsername);
        registry.add("spring.datasource.password", dataSource::getPassword);

        registry.add("service.baseUrl.auth", () -> "http://localhost:" + wireMockServer.port());
    }

    @DynamicPropertySource
    private static void wireMockProperties(DynamicPropertyRegistry registry) {
        registry.add("service.baseUrl.auth", () -> "http://localhost:8080");
    }
}