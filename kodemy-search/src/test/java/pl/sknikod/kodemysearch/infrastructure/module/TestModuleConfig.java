package pl.sknikod.kodemysearch.infrastructure.module;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialAddUpdateService;
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialSearchService;

@TestConfiguration
public class TestModuleConfig {
    @Bean
    public MaterialAddUpdateService materialAddUpdateService() {
        return Mockito.mock(MaterialAddUpdateService.class);
    }

    @Bean
    public MaterialSearchService materialSearchService() {
        return Mockito.mock(MaterialSearchService.class);
    }
}
