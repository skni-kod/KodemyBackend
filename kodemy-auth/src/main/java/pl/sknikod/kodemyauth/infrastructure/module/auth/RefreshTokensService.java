package pl.sknikod.kodemyauth.infrastructure.module.auth;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.configuration.SecurityConfiguration;
import pl.sknikod.kodemyauth.infrastructure.database.model.RefreshToken;
import pl.sknikod.kodemyauth.infrastructure.database.model.User;
import pl.sknikod.kodemyauth.infrastructure.database.handler.RefreshTokenStoreHandler;
import pl.sknikod.kodemyauth.infrastructure.module.auth.rest.model.RefreshTokensResponse;
import pl.sknikod.kodemycommon.exception.InternalError500Exception;
import pl.sknikod.kodemycommon.security.JwtProvider;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokensService {
    private final RefreshTokenStoreHandler refreshTokenRepositoryHandler;
    private final JwtProvider jwtProvider;
    private final SecurityConfiguration.RoleProperties roleProperties;

    public RefreshTokensResponse refresh(UUID refresh, UUID bearerJti) {
        return refreshTokenRepositoryHandler.findByTokenAndBearerJti(refresh, bearerJti)
                .flatMapTry(this::generateTokensAndInvalidate)
                .map(tokens -> new RefreshTokensResponse(
                        tokens._2.getToken().toString(), tokens._1.value()))
                .getOrElseThrow(th -> new InternalError500Exception());
    }

    private Try<Tuple2<JwtProvider.Token, RefreshToken>> generateTokensAndInvalidate(RefreshToken refreshToken) {
        return Try.of(() -> jwtProvider.generateUserToken(map(refreshToken.getUser())))
                .flatMapTry(bearerToken -> refreshTokenRepositoryHandler
                        .createAndGet(refreshToken.getUser(), bearerToken.id())
                        .map(newRefreshToken -> Tuple.of(bearerToken, newRefreshToken))
                        .onFailure(th -> log.error("Error during tokens generation", th)))
                .peek(unused -> refreshTokenRepositoryHandler.invalidate(refreshToken));
    }

    private JwtProvider.Input map(User user) {
        return new JwtProvider.Input(
                user.getId(),
                user.getUsername(),
                user.getIsExpired(),
                user.getIsLocked(),
                user.getIsCredentialsExpired(),
                user.getIsEnabled(),
                roleProperties.getAuthorities(user.getRole().getName().name())
        );
    }
}
