package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.exception.general.NotFoundException;
import pl.sknikod.kodemy.infrastructure.rest.model.response.SingleTypeResponse;
import pl.sknikod.kodemy.infrastructure.model.type.Type;
import pl.sknikod.kodemy.infrastructure.model.type.TypeRepository;

import java.util.List;

@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public abstract class TypeMapper {
    @Autowired
    protected TypeRepository typeRepository;

    public Type map(Long id) {
        return typeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NotFoundException.Format.entityId, Type.class, id));
    }

    public abstract List<SingleTypeResponse> map(List<Type> types);
}
