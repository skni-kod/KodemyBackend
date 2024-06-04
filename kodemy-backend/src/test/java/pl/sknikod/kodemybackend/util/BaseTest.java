package pl.sknikod.kodemybackend.util;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestExecutionListeners;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.sknikod.kodemybackend.configuration.SecurityBeanConfig;

@SpringBootTest
@Testcontainers
@ImportAutoConfiguration(value = {
        SecurityBeanConfig.class,
        UtilBeanConfig.class
})
@TestExecutionListeners(
        listeners = {WithUserPrincipalTestExecutionListener.class},
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public abstract class BaseTest {
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.1-alpine")
                .withDatabaseName("kodemy")
                .withUsername("postgres")
                .withPassword("postgres");
    }

    @DynamicPropertySource
    private static void containerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
