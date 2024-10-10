package pl.sknikod.kodemyauth.infrastructure.module.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.database.User;
import pl.sknikod.kodemyauth.infrastructure.dao.UserDao;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.exception.content.ExceptionMsgPattern;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;

@Component
@RequiredArgsConstructor
public class ChangeUserRoleService {
    private final UserDao userDao;

    public void change(Long userId, String roleName) {
        userDao.updateRole(userId, roleName)
                .onFailure(th -> {
                    throw new InternalError500Exception(ExceptionMsgPattern.PROCESS_FAILED_ENTITY, User.class);
                })
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
