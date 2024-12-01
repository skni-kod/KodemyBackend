package pl.sknikod.kodemyauth.infrastructure.module.oauth2;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemyauth.infrastructure.database.Permission;
import pl.sknikod.kodemyauth.infrastructure.database.Role;
import pl.sknikod.kodemyauth.infrastructure.database.RoleRepository;
import pl.sknikod.kodemyauth.infrastructure.database.User;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange.ProviderEngine;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange.ProviderUser;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange.Registration;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2UserPrincipal;
import pl.sknikod.kodemyauth.infrastructure.store.UserStore;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.exception.Validation400Exception;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2AuthorizeService {
    private final ProviderEngine providerEngine;
    private final RoleRepository roleRepository;
    private final UserStore userStore;

    public void authorize(Registration registrationId, Map<String, String> parameters) {
        Try.of(() -> {
            if (!parameters.containsKey("code")) {
                throw new Validation400Exception("Bad parameters map");
            }
            return providerEngine.createProviderUser(registrationId.getId(), parameters)
                    .map(this::createOrLoadUser)
                    .map(this::toUserPrincipal)
                    .orElse(null);
        }).getOrElseThrow(() -> new InternalError500Exception());
    }

    private Tuple2<User, ProviderUser> createOrLoadUser(ProviderUser providerUser) {
        return userStore.findByProviderUser(providerUser)
                .fold(unused -> Tuple.of(this.createNewUser(providerUser), providerUser), user -> Tuple.of(user, providerUser));
    }

    private User createNewUser(ProviderUser providerUser) {
        return userStore.save(providerUser)
                .orElse(null);
    }

    private OAuth2UserPrincipal toUserPrincipal(Tuple2<User, ProviderUser> userTuple2) {
        return Try.of(() -> roleRepository.findById(userTuple2._1.getRole().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Role::getPermissions)
                .onFailure(th -> log.error("Cannot retrieve authorities for role", th))
                .fold(
                        unused -> map(userTuple2._1, Collections.emptySet(), userTuple2._2.getAttributes()),
                        permissions -> map(userTuple2._1, permissions, userTuple2._2.getAttributes())
                );
    }

    private OAuth2UserPrincipal map(User user, Set<Permission> permissions, Map<String, Object> attributes) {
        return new OAuth2UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getIsExpired(),
                user.getIsLocked(),
                user.getIsCredentialsExpired(),
                user.getIsEnabled(),
                permissions.stream().map(Permission::getName).map(SimpleGrantedAuthority::new).toList(),
                attributes
        );
    }
}
