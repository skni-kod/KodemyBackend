package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import pl.sknikod.kodemy.infrastructure.rest.model.response.UserOAuth2MeResponse;
import pl.sknikod.kodemy.infrastructure.model.role.Role;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public abstract class RoleMapper {
    public abstract UserOAuth2MeResponse.RoleDetails map(Role role);
}
