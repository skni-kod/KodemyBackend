package pl.sknikod.kodemy.rest.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import pl.sknikod.kodemy.rest.response.UserOAuth2MeResponse;
import pl.sknikod.kodemy.user.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, uses = {
        RoleMapper.class
})
public abstract class UserMapper {
    public abstract UserOAuth2MeResponse map(User user);
}
