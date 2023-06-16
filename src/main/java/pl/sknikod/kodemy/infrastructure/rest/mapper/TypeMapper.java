package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sknikod.kodemy.exception.structure.NotFoundException;
import pl.sknikod.kodemy.infrastructure.model.type.Type;
import pl.sknikod.kodemy.infrastructure.model.type.TypeRepository;
import pl.sknikod.kodemy.infrastructure.rest.model.SingleTypeResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TypeMapper {
    @Autowired
    protected TypeRepository typeRepository;

    public Type map(Long id) {
        return typeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundException.Format.entityId, Type.class, id));
    }

    public abstract List<SingleTypeResponse> map(List<Type> types);
}
