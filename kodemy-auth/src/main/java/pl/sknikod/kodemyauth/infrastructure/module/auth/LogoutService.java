package pl.sknikod.kodemyauth.infrastructure.module.auth;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.store.RefreshTokenStore;
import pl.sknikod.kodemyauth.util.web.BearerHelper;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;
import pl.sknikod.kodemycommons.security.JwtProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutService implements BearerHelper {
    private final RefreshTokenStore refreshTokenRepositoryHandler;
    private final JwtProvider jwtProvider;

    public void logout(String authorizationHeader) {
        Try.of(() -> authorizationHeader)
                .map(this::extractBearer)
                .flatMap(jwtProvider::parseToken)
                .flatMap(token -> refreshTokenRepositoryHandler.invalidateByUserIdAnfBearerJti(token.getId(), token.getBearerId()))
                .onFailure(th -> log.error("Error during logout", th))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
