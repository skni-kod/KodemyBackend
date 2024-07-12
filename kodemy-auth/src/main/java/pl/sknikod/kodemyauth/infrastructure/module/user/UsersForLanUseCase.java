package pl.sknikod.kodemyauth.infrastructure.module.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.exception.ExceptionUtil;
import pl.sknikod.kodemyauth.infrastructure.database.handler.UserRepositoryHandler;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.SimpleUserResponse;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsersForLanUseCase {
    private final UserRepositoryHandler userRepositoryHandler;

    public List<SimpleUserResponse> getUsers(Set<Long> ids) {
        return userRepositoryHandler.findByIds(ids)
                .map(list -> list
                        .stream()
                        .map(obj -> new SimpleUserResponse((Long) obj[0], (String) obj[1]))
                        .toList()
                )
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
