package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemy.infrastructure.model.entity.User;
import pl.sknikod.kodemy.infrastructure.rest.model.UserInfoResponse;

@Mapper(componentModel = "spring", uses = {
        RoleMapper.class
})
public interface UserMapper {
    UserInfoResponse map(User user);
}
