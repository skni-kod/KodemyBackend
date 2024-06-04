package pl.sknikod.kodemysearch.util;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import pl.sknikod.kodemysearch.configuration.TestOpenSearchConfig;
import pl.sknikod.kodemysearch.configuration.TestSecurityConfig;
import pl.sknikod.kodemysearch.infrastructure.module.TestModuleConfig;

@SpringBootTest
@ImportAutoConfiguration(value = {
        TestSecurityConfig.class,
        TestOpenSearchConfig.class,
        TestModuleConfig.class
})
@TestExecutionListeners(
        listeners = {WithUserPrincipalTestExecutionListener.class},
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public abstract class BaseTest {
}
