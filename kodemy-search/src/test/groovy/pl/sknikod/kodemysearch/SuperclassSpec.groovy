package pl.sknikod.kodemysearch

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ContextConfiguration(classes = KodemySearchApplication.class)
@ImportAutoConfiguration(value = TestChannelBinderConfiguration.class)
@Testcontainers
abstract class SuperclassSpec extends Specification {

    protected static final WireMockServer WIREMOCK
    protected static int WIREMOCK_PORT = 9999

    static {
        WIREMOCK = new WireMockServer(
                WireMockConfiguration.options()
                        .port(WIREMOCK_PORT)
        )
    }

    @DynamicPropertySource
    private static void containerProperties(DynamicPropertyRegistry registry) {

        final String wiremockBaseUrl = "http://localhost:${WIREMOCK_PORT}"

        Map.of(
                "opensearch.host", wiremockBaseUrl,
                "opensearch.username", "admin",
                "opensearch.password", "admin",
                "opensearch.indices.materials.name", "materials",
                "opensearch.indices.materials.alias", "materials-alias",
                "service.baseUrl.gateway", "${wiremockBaseUrl}/gateway",
                "eureka.client.enabled", false,
        ).forEach {
            key, value -> registry.add(key, { value })
        }
    }
}