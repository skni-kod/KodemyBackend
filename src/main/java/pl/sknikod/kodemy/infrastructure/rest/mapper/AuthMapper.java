package pl.sknikod.kodemy.infrastructure.rest.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemy.infrastructure.model.entity.User;
import pl.sknikod.kodemy.infrastructure.rest.model.UserOAuth2MeResponse;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    UserOAuth2MeResponse map(User user);
}
