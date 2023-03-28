package pl.sknikod.kodemy.material;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.dto.MaterialCreateRequest;
import pl.sknikod.kodemy.dto.MaterialCreateResponse;
import pl.sknikod.kodemy.dto.mapper.MaterialMapper;
import pl.sknikod.kodemy.exception.GeneralException;

@Service
@AllArgsConstructor
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    public MaterialCreateResponse create(MaterialCreateRequest body){
        return Option.of(body)
                .map(materialMapper::map)
                .map(materialRepository::save)
                .map(materialMapper::map)
                .getOrElseThrow(() -> new GeneralException("Failed to processing"));
    }
}