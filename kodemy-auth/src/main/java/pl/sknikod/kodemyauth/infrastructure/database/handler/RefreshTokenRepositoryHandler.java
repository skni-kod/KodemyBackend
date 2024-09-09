package pl.sknikod.kodemyauth.infrastructure.database.handler;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.database.entity.RefreshToken;
import pl.sknikod.kodemyauth.infrastructure.database.entity.User;
import pl.sknikod.kodemyauth.infrastructure.database.repository.RefreshTokenRepository;
import pl.sknikod.kodemyauth.infrastructure.database.repository.UserRepository;
import pl.sknikod.kodemycommon.exception.InternalError500Exception;
import pl.sknikod.kodemycommon.exception.NotFound404Exception;
import pl.sknikod.kodemycommon.exception.content.ExceptionMsgPattern;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class RefreshTokenRepositoryHandler {
    private final RefreshTokenRepository refreshTokenRepository;
    private final Integer refreshTokenExpirationMin;
    private final UserRepository userRepository;

    public RefreshTokenRepositoryHandler(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${jwt.refresh.expiration-min:1440}") Integer refreshTokenExpirationMin,
            UserRepository userRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpirationMin = refreshTokenExpirationMin;
        this.userRepository = userRepository;
    }

    public Try<RefreshToken> createAndGet(long userId, UUID bearerId) {
        return Try.of(() -> userRepository.findById(userId))
                .map(Optional::get)
                .toTry(() -> new NotFound404Exception(ExceptionMsgPattern.ENTITY_NOT_FOUND_BY_PARAM, User.class, "id", userId))
                .map(user -> new RefreshToken(
                        UUID.randomUUID(),
                        bearerId,
                        LocalDateTime.now().plusMinutes(refreshTokenExpirationMin),
                        user
                ))
                .flatMapTry(this::saveEntity)
                .recoverWith(ex -> Try.failure(new InternalError500Exception()));
    }

    private Try<RefreshToken> saveEntity(RefreshToken refreshToken) {
        return Try.of(() -> refreshTokenRepository.save(refreshToken))
                .onFailure(th -> log.warn("Cannot save refresh token", th));
    }

    public Try<RefreshToken> createAndGet(User user, UUID bearerId) {
        return Try.of(() -> new RefreshToken(
                        UUID.randomUUID(),
                        bearerId,
                        LocalDateTime.now().plusMinutes(refreshTokenExpirationMin),
                        user
                ))
                .flatMapTry(this::saveEntity)
                .recoverWith(ex -> Try.failure(new InternalError500Exception()));
    }

    public Try<RefreshToken> findByTokenAndBearerJti(UUID refresh, UUID bearerJti) {
        return Option.of(refreshTokenRepository.findRTByTokenAndBearerJtiWithFetchUser(
                        refresh, bearerJti))
                .filter(refreshToken -> refreshToken.getExpiredDate().isAfter(LocalDateTime.now()))
                .toTry(() -> new NotFound404Exception(ExceptionMsgPattern.ENTITY_NOT_FOUND, RefreshToken.class))
                .onFailure(th -> log.error(th.getMessage(), th));
    }

    public void invalidate(RefreshToken refreshToken) {
        Try.of(() -> {
                    refreshTokenRepository.deleteById(refreshToken.getId());
                    return true;
                })
                .onSuccess(unused -> log.debug("Successfully remove refresh token ({})", refreshToken.getId()))
                .onFailure(th -> log.error("Failed to remove refresh token ({})", refreshToken.getId(), th));
    }
}
