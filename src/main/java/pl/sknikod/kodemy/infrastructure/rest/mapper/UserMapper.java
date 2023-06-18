package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemy.infrastructure.model.user.User;
import pl.sknikod.kodemy.infrastructure.rest.model.UserDetails;

@Mapper(componentModel = "spring", uses = {
        RoleMapper.class
})
public interface UserMapper {
    UserDetails map(User user);
}
