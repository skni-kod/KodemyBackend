package pl.sknikod.kodemyauth.infrastructure.module.auth;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.database.RefreshToken;
import pl.sknikod.kodemyauth.infrastructure.database.Role;
import pl.sknikod.kodemyauth.infrastructure.database.RoleRepository;
import pl.sknikod.kodemyauth.infrastructure.database.User;
import pl.sknikod.kodemyauth.infrastructure.module.auth.model.RefreshTokensResponse;
import pl.sknikod.kodemyauth.infrastructure.store.RefreshTokenStore;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;
import pl.sknikod.kodemycommons.security.JwtProvider;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokensService {
    private final RoleRepository roleRepository;
    private final RefreshTokenStore refreshTokenStore;
    private final JwtProvider jwtProvider;

    public RefreshTokensResponse refreshAccessToken(String grantType, String refreshToken) {
        return Try.of(() -> validateRequest(grantType, refreshToken))
                .flatMap(aBoolean -> refreshTokenStore.findByToken(refreshToken))
                .flatMap(this::generateTokensAndInvalidate)
                .map(tokens -> new RefreshTokensResponse(
                        tokens._2.getToken().toString(), tokens._1.value()))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private boolean validateRequest(String grantType, String refreshToken) {
        if (!"refresh_token".equals(grantType)) {
            throw new IllegalArgumentException();
        }
        if (!refreshTokenStore.isValidRefreshToken(refreshToken)) {
            throw new IllegalArgumentException();
        }
        return true;
    }

    private Try<Tuple2<JwtProvider.Token, RefreshToken>> generateTokensAndInvalidate(RefreshToken refreshToken) {
        return Try.of(() -> jwtProvider.generateUserToken(map(refreshToken.getUser())))
                .flatMapTry(bearerToken -> refreshTokenStore
                        .createAndGet(refreshToken.getUser(), bearerToken.id())
                        .map(newRefreshToken -> Tuple.of(bearerToken, newRefreshToken))
                        .onFailure(th -> log.error("Error during tokens generation", th)))
                .peek(unused -> refreshTokenStore.invalidate(refreshToken));
    }

    private JwtProvider.Input map(User user) {
        return new JwtProvider.Input(
                user.getId(),
                user.getUsername(),
                user.getIsExpired(),
                user.getIsLocked(),
                user.getIsCredentialsExpired(),
                user.getIsEnabled(),
                roleRepository.findById(user.getRole().getId()).map(Role::getPermissions)
                        .stream().flatMap(Collection::stream)
                        .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                        .collect(Collectors.toSet())
        );
    }
}
