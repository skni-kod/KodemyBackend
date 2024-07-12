package pl.sknikod.kodemysearch.infrastructure.module;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialAddUpdateUseCase;
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialSearchUseCase;

@TestConfiguration
public class TestModuleConfig {
    @Bean
    public MaterialAddUpdateUseCase materialAddUpdateUseCase() {
        return Mockito.mock(MaterialAddUpdateUseCase.class);
    }

    @Bean
    public MaterialSearchUseCase materialSearchUseCase() {
        return Mockito.mock(MaterialSearchUseCase.class);
    }
}
