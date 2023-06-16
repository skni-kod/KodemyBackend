package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemy.infrastructure.model.user.User;
import pl.sknikod.kodemy.infrastructure.rest.model.UserOAuth2MeResponse;

@Mapper(componentModel = "spring")
public abstract class AuthMapper {
    public abstract UserOAuth2MeResponse map(User user);
}
