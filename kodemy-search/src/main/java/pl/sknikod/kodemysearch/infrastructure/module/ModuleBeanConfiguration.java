package pl.sknikod.kodemysearch.infrastructure.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sknikod.kodemysearch.infrastructure.dao.MaterialSearchDao;
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialAddUpdateService;
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialSearchService;

@Configuration
public class ModuleBeanConfiguration {
    @Bean
    public MaterialAddUpdateService materialAddUpdateService(
            MaterialSearchDao materialSearchDao,
            ObjectMapper objectMapper,
            MaterialAddUpdateService.MaterialIndexDataMapper materialIndexDataMapper
    ) {
        return new MaterialAddUpdateService(materialSearchDao, objectMapper, materialIndexDataMapper);
    }

    @Bean
    public MaterialSearchService materialSearchService(
            MaterialSearchDao materialSearchDao, MaterialSearchService.MaterialSearchMapper materialSearchMapper) {
        return new MaterialSearchService(materialSearchDao, materialSearchMapper);
    }
}
