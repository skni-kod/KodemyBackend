package pl.sknikod.kodemy.infrastructure.rest.mapper;

import io.vavr.control.Option;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.exception.general.NotFoundException;
import pl.sknikod.kodemy.infrastructure.rest.model.UserDetails;
import pl.sknikod.kodemy.infrastructure.model.user.User;
import pl.sknikod.kodemy.infrastructure.model.user.UserPrincipal;
import pl.sknikod.kodemy.infrastructure.model.user.UserRepository;

import java.util.Optional;

@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, uses = {
        RoleMapper.class
})
public abstract class UserMapper {
    @Autowired
    private UserRepository userRepository;

    public abstract UserDetails map(User user);

    protected User mapUserFromContext() {
        return Option.of(UserPrincipal.getCurrentSessionUser())
                .map(UserPrincipal::getId)
                .map(userRepository::findById)
                .map(Optional::get)
                .getOrElseThrow(() -> new NotFoundException("Session user not found"));
    }
}