package pl.sknikod.kodemyauth

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ContextConfiguration(classes = KodemyAuthApplication.class)
@ImportAutoConfiguration(value = TestChannelBinderConfiguration.class)
@Testcontainers
abstract class SuperclassSpec extends Specification {
    @Container
    private static final PostgreSQLContainer<?> DATASOURCE
    private static final WireMockServer WIREMOCK
    private static int WIREMOCK_PORT = 9999

    static {
        DATASOURCE = new PostgreSQLContainer<>("postgres:14.1-alpine")
        WIREMOCK = new WireMockServer(WireMockConfiguration.options().port(WIREMOCK_PORT))
    }

    @DynamicPropertySource
    private static void containerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DATASOURCE::getJdbcUrl)
        registry.add("spring.datasource.username", DATASOURCE::getUsername)
        registry.add("spring.datasource.password", DATASOURCE::getPassword)

        final String wiremockBaseUrl = "http://localhost:${WIREMOCK_PORT}"
        Map.of(
                "service.baseUrl.gateway", "${wiremockBaseUrl}/gateway",
                "security.oauth2.client.registration.github.tokenUri", "${wiremockBaseUrl}/login/oauth/access_token",
                "security.oauth2.client.registration.github.authorizationUri", "${wiremockBaseUrl}/login/oauth/authorize",
                "security.oauth2.client.registration.github.userInfoEndpoint.uri", "${wiremockBaseUrl}/user",
                "eureka.client.enabled", false,
        ).forEach {
            key, value -> registry.add(key, { value })
        }
    }
}