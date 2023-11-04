package pl.sknikod.kodemy.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemy.infrastructure.common.entity.Type;
import pl.sknikod.kodemy.infrastructure.type.rest.SingleTypeResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TypeMapper {

    List<SingleTypeResponse> map(List<Type> types);
}
