package pl.sknikod.kodemyauth.infrastructure.module.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.exception.ExceptionPattern;
import pl.sknikod.kodemyauth.exception.ExceptionUtil;
import pl.sknikod.kodemyauth.exception.structure.ServerProcessingException;
import pl.sknikod.kodemyauth.infrastructure.database.entity.Role;
import pl.sknikod.kodemyauth.infrastructure.database.entity.User;
import pl.sknikod.kodemyauth.infrastructure.database.handler.UserRepositoryHandler;

@Component
@RequiredArgsConstructor
public class ChangeUserRoleUseCase {
    private final UserRepositoryHandler userRepositoryHandler;

    public void change(Long userId, Role.RoleName roleName) {
        userRepositoryHandler.updateRole(userId, roleName)
                .onFailure(th -> {
                    throw new ServerProcessingException(ExceptionPattern.PROCESS_FAILED_ENTITY, User.class);
                })
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
