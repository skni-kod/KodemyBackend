package pl.sknikod.kodemyauth.infrastructure.module.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import pl.sknikod.kodemyauth.infrastructure.database.entity.Role;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.UserInfoResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {
    UserInfoResponse.RoleDetails map(Role role);
}
