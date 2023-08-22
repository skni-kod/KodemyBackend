package pl.sknikod.kodemy.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemy.infrastructure.common.entity.Role;
import pl.sknikod.kodemy.infrastructure.user.rest.UserInfoResponse;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    UserInfoResponse.RoleDetails map(Role role);
}
