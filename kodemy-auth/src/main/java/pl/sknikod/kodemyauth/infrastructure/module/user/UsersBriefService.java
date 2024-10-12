package pl.sknikod.kodemyauth.infrastructure.module.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.dao.UserDao;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.SimpleUserResponse;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsersBriefService {
    private final UserDao userDao;

    public List<SimpleUserResponse> getUserBrief(Set<Long> ids) {
        return userDao.findByIds(ids)
                .map(list -> list
                        .stream()
                        .map(obj -> new SimpleUserResponse((Long) obj[0], (String) obj[1]))
                        .toList()
                )
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
