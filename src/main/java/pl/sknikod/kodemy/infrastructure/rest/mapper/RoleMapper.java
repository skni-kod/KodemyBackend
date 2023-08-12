package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemy.infrastructure.model.entity.Role;
import pl.sknikod.kodemy.infrastructure.rest.model.UserInfoResponse;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    UserInfoResponse.RoleDetails map(Role role);
}
