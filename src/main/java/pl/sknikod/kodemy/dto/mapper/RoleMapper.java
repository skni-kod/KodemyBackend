package pl.sknikod.kodemy.dto.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import pl.sknikod.kodemy.dto.UserOAuth2MeResponse;
import pl.sknikod.kodemy.role.Role;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public abstract class RoleMapper {
    public abstract UserOAuth2MeResponse.RoleDetails map(Role role);
}
