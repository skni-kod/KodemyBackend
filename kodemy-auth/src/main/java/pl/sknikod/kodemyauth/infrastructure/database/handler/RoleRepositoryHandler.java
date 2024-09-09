package pl.sknikod.kodemyauth.infrastructure.database.handler;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.database.entity.Role;
import pl.sknikod.kodemyauth.infrastructure.database.repository.RoleRepository;
import pl.sknikod.kodemycommon.exception.NotFound404Exception;
import pl.sknikod.kodemycommon.exception.content.ExceptionMsgPattern;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleRepositoryHandler {
    private final RoleRepository roleRepository;

    public Try<Role> findByRoleName(String roleName) throws RuntimeException {
        return Try.of(() -> roleRepository.findByName(Role.RoleName.valueOf(roleName)))
                .map(Optional::get)
                .toTry(() -> new NotFound404Exception(ExceptionMsgPattern.ENTITY_NOT_FOUND, Role.class))
                .onFailure(th -> log.error(th.getMessage(), roleName));
    }
}
