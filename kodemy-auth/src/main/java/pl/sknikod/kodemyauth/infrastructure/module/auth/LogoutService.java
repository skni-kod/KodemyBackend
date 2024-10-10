package pl.sknikod.kodemyauth.infrastructure.module.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.dao.RefreshTokenDao;
import pl.sknikod.kodemycommon.exception.InternalError500Exception;
import pl.sknikod.kodemycommon.security.UserPrincipal;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutService {
    private final RefreshTokenDao refreshTokenRepositoryHandler;

    public Boolean logout(UserPrincipal userPrincipal, UUID bearerJti) {
        return refreshTokenRepositoryHandler.invalidateByUserIdAnfBearerJti(userPrincipal.getId(), bearerJti)
                .getOrElseThrow(th -> new InternalError500Exception());
    }
}
