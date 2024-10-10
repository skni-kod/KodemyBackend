package pl.sknikod.kodemyauth.infrastructure.module.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.database.model.Role;
import pl.sknikod.kodemyauth.infrastructure.database.model.User;
import pl.sknikod.kodemyauth.infrastructure.database.handler.UserStoreHandler;
import pl.sknikod.kodemycommon.exception.InternalError500Exception;
import pl.sknikod.kodemycommon.exception.content.ExceptionMsgPattern;
import pl.sknikod.kodemycommon.exception.content.ExceptionUtil;

@Component
@RequiredArgsConstructor
public class ChangeUserRoleUseCase {
    private final UserStoreHandler userStoreHandler;

    public void change(Long userId, String roleName) {
        userStoreHandler.updateRole(userId, roleName)
                .onFailure(th -> {
                    throw new InternalError500Exception(ExceptionMsgPattern.PROCESS_FAILED_ENTITY, User.class);
                })
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
