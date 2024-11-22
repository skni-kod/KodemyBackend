package pl.sknikod.kodemyauth;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.sknikod.kodemycommons.security.configuration.JwtConfiguration;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@ContextConfiguration(classes = KodemyAuthApplication.class)
@ImportAutoConfiguration(value = TestChannelBinderConfiguration.class)
@Import({JwtConfiguration.class})
@Testcontainers
public class SuperclassTest {
    @Container
    private static final PostgreSQLContainer<?> dataSource;

    static {
        dataSource = new PostgreSQLContainer<>("postgres:14.1-alpine");
    }

    @DynamicPropertySource
    private static void containerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", dataSource::getJdbcUrl);
        registry.add("spring.datasource.username", dataSource::getUsername);
        registry.add("spring.datasource.password", dataSource::getPassword);
    }
}