package pl.sknikod.kodemybackend.infrastructure.module.type;

import lombok.AllArgsConstructor;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.TypeMapper;
import pl.sknikod.kodemybackend.infrastructure.database.handler.TypeRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.module.type.model.SingleTypeResponse;
import pl.sknikod.kodemycommon.exception.content.ExceptionUtil;

import java.util.List;

@AllArgsConstructor
public class TypeUseCase {
    private final TypeRepositoryHandler typeRepositoryHandler;
    private final TypeMapper typeMapper;

    public List<SingleTypeResponse> getAllTypes() {
        return typeRepositoryHandler.findAll()
                .map(typeMapper::map)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
