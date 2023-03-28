package pl.sknikod.kodemy.dto.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.sknikod.kodemy.dto.UserOAuth2MeResponse;
import pl.sknikod.kodemy.user.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, uses = {
        RoleMapper.class
})
public abstract class UserMapper {
    public abstract UserOAuth2MeResponse map(User user);

    protected User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
