package pl.sknikod.kodemyauth.infrastructure.module.common.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemyauth.infrastructure.database.entity.User;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.UserInfoResponse;

@Mapper(componentModel = "spring", uses = {
        RoleMapper.class
})
public interface UserMapper {
    UserInfoResponse map(User user);
}
