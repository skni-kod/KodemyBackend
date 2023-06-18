package pl.sknikod.kodemy.infrastructure.rest;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.model.type.Type;
import pl.sknikod.kodemy.infrastructure.model.type.TypeRepository;
import pl.sknikod.kodemy.infrastructure.rest.mapper.TypeMapper;
import pl.sknikod.kodemy.infrastructure.rest.model.SingleTypeResponse;

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
