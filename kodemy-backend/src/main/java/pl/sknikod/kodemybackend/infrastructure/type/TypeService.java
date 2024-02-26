package pl.sknikod.kodemybackend.infrastructure.type;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Type;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.TypeMapper;
import pl.sknikod.kodemybackend.infrastructure.common.repository.TypeRepository;
import pl.sknikod.kodemybackend.infrastructure.type.rest.SingleTypeResponse;

import java.util.List;

@Service
@AllArgsConstructor
public class TypeService {

    private final TypeRepository typeRepository;
    private final TypeMapper typeMapper;

    public List<SingleTypeResponse> getAllTypes() {
        return Option.of(typeRepository.findAll())
                .map(typeMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Type.class));
    }
}
