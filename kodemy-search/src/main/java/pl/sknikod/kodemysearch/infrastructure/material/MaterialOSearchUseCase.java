package pl.sknikod.kodemysearch.infrastructure.material;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.update.UpdateRequest;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

import static pl.sknikod.kodemysearch.configuration.OpenSearchConfig.*;

@Component
@RequiredArgsConstructor
@Slf4j
@DependsOn("rabbitConfig")
public class MaterialOSearchUseCase {
    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;
    private final IndexManager indexManager;
    private final IndexProperties indexProperties;
    private final TypeReference<Map<String, ?>> MAP_TYPE_REF = new TypeReference<>() {
    };

    private String checkAndGetIndex() {
        var index = indexProperties.getIndex();
        indexManager.createIndexIfNotExists(index);
        return index.getName();
    }

    @RabbitListener(queues = "${app.rabbit.queues.m-created.name}")
    private void index(@Payload Object message) {
        var index = checkAndGetIndex();
        Try.of(() -> objectMapper.convertValue(((Message) message).getBody(), MAP_TYPE_REF))
                .map(jsonObject -> new IndexRequest(index)
                        .id(jsonObject.get("id").toString())
                        .source(jsonObject)
                )
                .onSuccess(request -> Try.of(() -> restHighLevelClient.index(request, REQUEST_OPTIONS))
                        .onFailure(ex -> log.error(ex.getMessage()))
                );
    }

    @RabbitListener(queues = "${app.rabbit.queues.m-updated.name}")
    private void reindex(@Payload Object message) {
        var index = checkAndGetIndex();
        Try.of(() -> objectMapper.convertValue(((Message) message).getBody(), MAP_TYPE_REF))
                .map(map -> new UpdateRequest(index, (String) map.get("id"))
                        .doc(map))
                .flatMap(request -> Try.of(() -> restHighLevelClient.update(request, REQUEST_OPTIONS))
                        .onFailure(ex -> log.error(ex.getMessage())))
                .onFailure(ex -> log.error(ex.getMessage()));
    }
}
