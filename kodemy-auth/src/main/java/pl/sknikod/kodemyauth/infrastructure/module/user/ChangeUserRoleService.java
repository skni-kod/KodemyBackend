package pl.sknikod.kodemyauth.infrastructure.module.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.store.UserStore;
import pl.sknikod.kodemyauth.infrastructure.database.User;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.exception.content.ExceptionMsgPattern;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;

@Component
@RequiredArgsConstructor
public class ChangeUserRoleService {
    private final UserStore userStore;

    public void change(Long userId, String roleName) {
        userStore.updateRole(userId, roleName)
                .onFailure(th -> {
                    throw new InternalError500Exception(ExceptionMsgPattern.PROCESS_FAILED_ENTITY, User.class);
                })
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
