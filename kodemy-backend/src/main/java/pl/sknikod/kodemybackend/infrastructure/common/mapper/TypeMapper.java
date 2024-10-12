package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import pl.sknikod.kodemybackend.infrastructure.database.Type;
import pl.sknikod.kodemybackend.infrastructure.module.type.model.SingleTypeResponse;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TypeMapper {

    List<SingleTypeResponse> map(List<Type> types);
}
