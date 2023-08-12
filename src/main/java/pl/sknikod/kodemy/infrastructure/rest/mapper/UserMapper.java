package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemy.infrastructure.model.entity.User;
import pl.sknikod.kodemy.infrastructure.rest.model.UserDetails;
import pl.sknikod.kodemy.infrastructure.rest.model.UserInfoResponse;

@Mapper(componentModel = "spring", uses = {
        RoleMapper.class
})
public interface UserMapper {
    UserDetails map(User user);

    User map(UserInfoResponse user);

    UserInfoResponse infoMap(User user);
}
