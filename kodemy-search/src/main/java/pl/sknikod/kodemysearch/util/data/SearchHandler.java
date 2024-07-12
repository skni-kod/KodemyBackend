package pl.sknikod.kodemysearch.util.data;

import io.vavr.control.Try;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.core.UpdateResponse;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialIndexData;

public interface SearchHandler<T extends IndexData> {
    Try<IndexResponse> save(T object);

    Try<UpdateResponse<T>> update(T object);
}
