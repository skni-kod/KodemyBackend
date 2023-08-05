package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemy.infrastructure.model.entity.Type;
import pl.sknikod.kodemy.infrastructure.rest.model.SingleTypeResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TypeMapper {

    List<SingleTypeResponse> map(List<Type> types);
}
