package pl.sknikod.kodemyauth.infrastructure.module.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.database.entity.Role;
import pl.sknikod.kodemyauth.infrastructure.database.entity.User;
import pl.sknikod.kodemyauth.infrastructure.database.handler.UserRepositoryHandler;
import pl.sknikod.kodemycommon.exception.InternalError500Exception;
import pl.sknikod.kodemycommon.exception.content.ExceptionMsgPattern;
import pl.sknikod.kodemycommon.exception.content.ExceptionUtil;

@Component
@RequiredArgsConstructor
public class ChangeUserRoleUseCase {
    private final UserRepositoryHandler userRepositoryHandler;

    public void change(Long userId, Role.RoleName roleName) {
        userRepositoryHandler.updateRole(userId, roleName)
                .onFailure(th -> {
                    throw new InternalError500Exception(ExceptionMsgPattern.PROCESS_FAILED_ENTITY, User.class);
                })
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
