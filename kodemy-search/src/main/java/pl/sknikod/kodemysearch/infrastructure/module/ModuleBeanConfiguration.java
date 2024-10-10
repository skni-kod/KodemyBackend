package pl.sknikod.kodemysearch.infrastructure.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pl.sknikod.kodemysearch.infrastructure.dao.MaterialSearchDao;
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialAddUpdateService;
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialSearchService;

@Configuration
public class ModuleBeanConfiguration {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

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
