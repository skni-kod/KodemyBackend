package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Type;
import pl.sknikod.kodemybackend.infrastructure.type.rest.SingleTypeResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TypeMapper {

    List<SingleTypeResponse> map(List<Type> types);
}
