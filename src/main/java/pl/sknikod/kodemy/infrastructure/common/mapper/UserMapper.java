package pl.sknikod.kodemy.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemy.infrastructure.common.entity.User;
import pl.sknikod.kodemy.infrastructure.user.rest.UserInfoResponse;

@Mapper(componentModel = "spring", uses = {
        RoleMapper.class
})
public interface UserMapper {
    UserInfoResponse map(User user);
}
