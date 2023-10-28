package pl.sknikod.kodemysearch.infrastructure.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import org.mapstruct.Mapper;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import pl.sknikod.kodemysearch.exception.structure.ServerProcessingException;
import pl.sknikod.kodemysearch.infrastructure.search.rest.MaterialResponse;

import java.util.List;
import java.util.stream.StreamSupport;

@Mapper(componentModel = "spring")
public interface MaterialSearchMapper {
    ObjectMapper objectMapper = new ObjectMapper();

    MaterialResponse map(QueueConsumer.MaterialEvent event);

    default List<MaterialResponse> map(SearchHits hits) {
        return StreamSupport.stream(hits.spliterator(), false)
                .map(SearchHit::getSourceAsString)
                .map(this::map)
                .toList();
    }

    private MaterialResponse map(String source) {
        return Try.of(() -> objectMapper.readValue(source, MaterialResponse.class))
                .onFailure(ex -> {
                    throw new ServerProcessingException(ex.getMessage());
                })
                .get();
    }
}
