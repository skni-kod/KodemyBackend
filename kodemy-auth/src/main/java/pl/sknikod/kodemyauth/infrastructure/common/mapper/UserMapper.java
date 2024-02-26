package pl.sknikod.kodemyauth.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemyauth.infrastructure.common.entity.User;
import pl.sknikod.kodemyauth.infrastructure.user.rest.UserInfoResponse;

@Mapper(componentModel = "spring", uses = {
        RoleMapper.class
})
public interface UserMapper {
    UserInfoResponse map(User user);
}
