package pl.sknikod.kodemyauth.infrastructure.store;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.database.RefreshToken;
import pl.sknikod.kodemyauth.infrastructure.database.RefreshTokenRepository;
import pl.sknikod.kodemyauth.infrastructure.database.User;
import pl.sknikod.kodemyauth.infrastructure.database.UserRepository;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.exception.NotFound404Exception;
import pl.sknikod.kodemycommons.exception.Validation400Exception;
import pl.sknikod.kodemycommons.exception.content.ExceptionMsgPattern;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class RefreshTokenStore {
    private final RefreshTokenRepository refreshTokenRepository;
    private final Integer refreshTokenExpirationMin;
    private final UserRepository userRepository;

    public RefreshTokenStore(
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
                .toTry(() -> new NotFound404Exception(ExceptionMsgPattern.ENTITY_NOT_FOUND_BY_PARAM, User.class.getSimpleName(), "id", userId))
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

    public boolean isValidRefreshToken(String refreshToken) {
        return Try.of(() -> refreshToken)
                .mapTry(UUID::fromString)
                .flatMap(token -> {
                    if (refreshTokenRepository.findByTokenAndExpiredDateAfter(token, LocalDateTime.now()).isEmpty()) {
                        return Try.failure(new Validation400Exception("Refresh token expired or invalidate"));
                    }
                    return Try.success(true);
                })
                .onFailure(th -> log.warn(th.getMessage()))
                .getOrElse(false);
    }

    public Try<RefreshToken> findByToken(String refreshToken) {
        return Option.of(refreshTokenRepository.findRTByTokenWithFetchUser(UUID.fromString(refreshToken)))
                .filter(token -> token.getExpiredDate().isAfter(LocalDateTime.now()))
                .toTry(() -> new NotFound404Exception(ExceptionMsgPattern.ENTITY_NOT_FOUND, RefreshToken.class))
                .onFailure(th -> log.error(th.getMessage(), th));
    }

    public Try<Boolean> invalidateByUserIdAnfBearerJti(Long userId, UUID bearerJti) {
        return Try.of(() -> {
                    refreshTokenRepository.deleteRTByUserIdAndBearerJti(userId, bearerJti);
                    return true;
                })
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
