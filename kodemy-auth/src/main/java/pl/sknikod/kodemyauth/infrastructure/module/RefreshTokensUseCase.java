package pl.sknikod.kodemyauth.infrastructure.module;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.configuration.SecurityConfig;
import pl.sknikod.kodemyauth.exception.structure.ServerProcessingException;
import pl.sknikod.kodemyauth.infrastructure.database.entity.RefreshToken;
import pl.sknikod.kodemyauth.infrastructure.database.entity.User;
import pl.sknikod.kodemyauth.infrastructure.database.handler.RefreshTokenRepositoryHandler;
import pl.sknikod.kodemyauth.infrastructure.module.auth.rest.model.RefreshTokensResponse;
import pl.sknikod.kodemyauth.util.auth.JwtService;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokensUseCase {
    private final RefreshTokenRepositoryHandler refreshTokenRepositoryHandler;
    private final JwtService jwtService;
    private final SecurityConfig.RoleProperties roleProperties;

    public RefreshTokensResponse refresh(UUID refresh, UUID bearerJti) {
        return refreshTokenRepositoryHandler.findByTokenAndBearerJti(refresh, bearerJti)
                .flatMapTry(this::generateTokensAndInvalidate)
                .map(tokens -> new RefreshTokensResponse(
                        tokens._2.getToken().toString(), tokens._1.value()))
                .getOrElseThrow(th -> new ServerProcessingException());
    }

    private Try<Tuple2<JwtService.Token, RefreshToken>> generateTokensAndInvalidate(RefreshToken refreshToken) {
        return Try.of(() -> jwtService.generateUserToken(map(refreshToken.getUser())))
                .flatMapTry(bearerToken -> refreshTokenRepositoryHandler
                        .createAndGet(refreshToken.getUser(), bearerToken.id())
                        .map(newRefreshToken -> Tuple.of(bearerToken, newRefreshToken))
                        .onFailure(th -> log.error("Error during tokens generation", th)))
                .peek(unused -> refreshTokenRepositoryHandler.invalidate(refreshToken));
    }

    private JwtService.Input map(User user) {
        return new JwtService.Input(
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
