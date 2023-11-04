package pl.sknikod.kodemy.infrastructure.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.infrastructure.material.MaterialConfig;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;

@Configuration
@Slf4j
public class SearchConfig {
    public static final String ALIAS_SUFFIX = "-alias";
    public static final String MATERIAL_INDEX = "material";

    @Component
    @RequiredArgsConstructor
    public static class Executor {
        private final SearchService searchService;

        @RabbitListener(queues = MaterialConfig.NAME_CREATED)
        private void index(@Payload MaterialSearchObject material) {
            searchService.indexMaterial(material);
        }

        @RabbitListener(queues = MaterialConfig.NAME_UPDATED)
        private void reindex(@Payload MaterialSearchObject material) {
            searchService.reindexMaterial(
                    material.getId().toString(),
                    material
            );
        }
    }
}
