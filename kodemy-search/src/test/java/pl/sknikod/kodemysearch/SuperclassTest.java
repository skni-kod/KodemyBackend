package pl.sknikod.kodemysearch;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import pl.sknikod.kodemycommons.security.configuration.JwtConfiguration;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@ContextConfiguration(classes = KodemyBackendApplication.class)
@ImportAutoConfiguration(value = TestChannelBinderConfiguration.class)
@Import({JwtConfiguration.class})
public class SuperclassTest {
}