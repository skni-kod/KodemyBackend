package pl.sknikod.kodemybackend.infrastructure.module.type;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.infrastructure.mapper.TypeMapper;
import pl.sknikod.kodemybackend.infrastructure.module.type.model.SingleTypeResponse;
import pl.sknikod.kodemybackend.infrastructure.store.TypeStore;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;

import java.util.List;

@Service
@AllArgsConstructor
public class TypeService {
    private final TypeStore typeStore;
    private final TypeMapper typeMapper;

    public List<SingleTypeResponse> getAllTypes() {
        return typeStore.findAll()
                .map(typeMapper::map)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
