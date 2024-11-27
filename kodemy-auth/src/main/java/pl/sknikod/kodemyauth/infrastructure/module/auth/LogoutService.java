package pl.sknikod.kodemyauth.infrastructure.module.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.store.RefreshTokenStore;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.security.UserPrincipal;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutService {
    private final RefreshTokenStore refreshTokenRepositoryHandler;

    public Boolean logout(UserPrincipal userPrincipal, UUID bearerJti) {
        return refreshTokenRepositoryHandler.invalidateByUserIdAnfBearerJti(userPrincipal.getId(), bearerJti)
                .getOrElseThrow(th -> new InternalError500Exception());
    }
}
