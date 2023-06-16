package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemy.infrastructure.model.user.User;
import pl.sknikod.kodemy.infrastructure.rest.model.UserDetails;

@Mapper(componentModel = "spring", uses = {
        RoleMapper.class
})
public abstract class UserMapper {
    public abstract UserDetails map(User user);
}
