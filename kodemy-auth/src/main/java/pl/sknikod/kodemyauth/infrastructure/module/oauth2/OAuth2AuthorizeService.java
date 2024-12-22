package pl.sknikod.kodemyauth.infrastructure.module.oauth2;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemyauth.infrastructure.database.*;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange.ExchangeEngine;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange.ProviderUser;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange.Registration;
import pl.sknikod.kodemyauth.infrastructure.store.RefreshTokenStore;
import pl.sknikod.kodemyauth.infrastructure.store.UserStore;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;
import pl.sknikod.kodemycommons.security.JwtProvider;
import pl.sknikod.kodemycommons.security.UserPrincipal;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2AuthorizeService {
    private final ExchangeEngine exchangeEngine;
    private final RoleRepository roleRepository;
    private final UserStore userStore;
    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;

    public AuthorizeResponse authorize(Registration registrationId, Map<String, String> parameters) {
        return Try.of(() -> {
                    if (parameters.get("code") == null) {
                        throw new InternalError500Exception();
                    }
                    return true;
                })
                .flatMap(aBoolean -> exchangeEngine.createProviderUser(registrationId.getId(), parameters))
                .map(this::createOrLoadUser)
                .map(this::toUserPrincipal)
                .map(this::generateTokens)
                .map(tokens -> new AuthorizeResponse(tokens._1.value(), tokens._2.getToken().toString()))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private Tuple2<User, ProviderUser> createOrLoadUser(ProviderUser providerUser) {
        return userStore.findByProviderUser(providerUser).fold(
                th -> Tuple.of(this.createNewUser(providerUser), providerUser),
                user -> Tuple.of(user, providerUser)
        );
    }

    private User createNewUser(ProviderUser providerUser) {
        return userStore.save(providerUser)
                .orElse(null);
    }

    private UserPrincipal toUserPrincipal(Tuple2<User, ProviderUser> userTuple2) {
        return Try.of(() -> roleRepository.findById(userTuple2._1.getRole().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Role::getPermissions)
                .onFailure(th -> log.error("Cannot retrieve authorities for role", th))
                .fold(
                        th -> map(userTuple2._1, Collections.emptySet()),
                        permissions -> map(userTuple2._1, permissions)
                );
    }

    private UserPrincipal map(User user, Set<Permission> permissions) {
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getIsExpired(),
                user.getIsLocked(),
                user.getIsCredentialsExpired(),
                user.getIsEnabled(),
                permissions.stream().map(Permission::getName).map(SimpleGrantedAuthority::new).toList()
        );
    }

    private Tuple2<JwtProvider.Token, RefreshToken> generateTokens(UserPrincipal userPrincipal) {
        return Try.of(() -> jwtProvider.generateUserToken(mapToJwtInput(userPrincipal))).flatMapTry(bearerToken -> {
            return refreshTokenStore.createAndGet(userPrincipal.getId(), bearerToken.id())
                    .map(newRefreshToken -> Tuple.of(bearerToken, newRefreshToken))
                    .onFailure(th -> log.error("Error during tokens generation", th));
        }).getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private JwtProvider.Input mapToJwtInput(UserPrincipal user) {
        return new JwtProvider.Input(
                user.getId(),
                user.getUsername(),
                !user.isAccountNonExpired(),
                !user.isAccountNonLocked(),
                !user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getAuthorities()
        );
    }

    @Value
    public static class AuthorizeResponse {
        String accessToken;
        String refreshToken;
    }
}
