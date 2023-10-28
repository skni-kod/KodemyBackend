package pl.sknikod.kodemyauth.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Role;
import pl.sknikod.kodemyauth.infrastructure.user.rest.UserInfoResponse;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    UserInfoResponse.RoleDetails map(Role role);
}
