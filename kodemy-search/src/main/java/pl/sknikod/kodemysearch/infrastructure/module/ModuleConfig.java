package pl.sknikod.kodemysearch.infrastructure.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialSearchHandler;
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialAddUpdateUseCase;
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialSearchUseCase;

@Configuration
public class ModuleConfig {
    @Bean
    public MaterialAddUpdateUseCase materialAddUpdateUseCase(
            MaterialSearchHandler materialSearchHandler,
            ObjectMapper objectMapper,
            MaterialAddUpdateUseCase.MaterialIndexDataMapper materialIndexDataMapper
    ) {
        return new MaterialAddUpdateUseCase(materialSearchHandler, objectMapper, materialIndexDataMapper);
    }

    @Bean
    public MaterialSearchUseCase materialSearchUseCase(
            MaterialSearchHandler materialSearchHandler, MaterialSearchUseCase.MaterialSearchMapper materialSearchMapper) {
        return new MaterialSearchUseCase(materialSearchHandler, materialSearchMapper);
    }
}
