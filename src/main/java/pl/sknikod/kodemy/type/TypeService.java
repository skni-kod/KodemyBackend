package pl.sknikod.kodemy.type;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.general.ServerProcessingException;
import pl.sknikod.kodemy.rest.mapper.TypeMapper;
import pl.sknikod.kodemy.rest.response.SingleTypeResponse;

import java.util.List;

@Service
@AllArgsConstructor
public class TypeService {

    private final TypeRepository typeRepository;
    private final TypeMapper typeMapper;

    public List<SingleTypeResponse> getAllTypes() {
        return Option.of(typeRepository.findAll())
                .map(typeMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.processFailed, Type.class));
    }
}
