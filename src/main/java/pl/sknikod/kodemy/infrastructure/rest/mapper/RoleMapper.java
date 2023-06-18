package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemy.infrastructure.model.role.Role;
import pl.sknikod.kodemy.infrastructure.rest.model.UserOAuth2MeResponse;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    UserOAuth2MeResponse.RoleDetails map(Role role);
}
