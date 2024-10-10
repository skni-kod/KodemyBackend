package pl.sknikod.kodemyauth.infrastructure.dao;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.configuration.SecurityConfiguration;
import pl.sknikod.kodemyauth.infrastructure.database.Provider;
import pl.sknikod.kodemyauth.infrastructure.database.Role;
import pl.sknikod.kodemyauth.infrastructure.database.User;
import pl.sknikod.kodemyauth.infrastructure.database.RoleRepository;
import pl.sknikod.kodemyauth.infrastructure.database.UserRepository;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.OAuth2Provider;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.exception.NotFound404Exception;
import pl.sknikod.kodemycommons.exception.content.ExceptionMsgPattern;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDao {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SecurityConfiguration.RoleProperties roleProperties;

    public Optional<User> save(OAuth2Provider.User providerUser) {
        return this.fetchRole(roleProperties.getPrimary())
                .map(role -> new User(
                        providerUser.getUsername(), providerUser.getEmail(),
                        providerUser.getPhoto(), role))
                .map(user -> {
                    var provider = new Provider(
                            providerUser.getPrincipalId(), providerUser.getProvider(),
                            providerUser.getEmail(), providerUser.getPhoto(), user
                    );
                    user.setProviders(Set.of(provider));
                    return user;
                })
                .flatMap(user -> Try.of(() -> userRepository.save(user))
                        .onFailure(th -> log.error("Cannot save user", th))
                )
                .toJavaOptional();
    }

    private Try<Role> fetchRole(String roleName) {
        return Try.of(() -> roleRepository.findByName(roleName))
                .map(Optional::get)
                .toTry(() -> new NotFound404Exception(ExceptionMsgPattern.ENTITY_NOT_FOUND, Role.class))
                .onFailure(th -> log.error(th.getMessage()));
    }

    public Try<User> findByProviderUser(OAuth2Provider.User providerUser) {
        return Option.of(userRepository.findUserByPrincipalIdAndAuthProvider(
                        providerUser.getPrincipalId(), providerUser.getProvider()
                ))
                .toTry(() -> new NotFound404Exception(ExceptionMsgPattern.ENTITY_NOT_FOUND, User.class));
    }

    public Try<User> findById(Long id) {
        return Try.of(() -> userRepository.findById(id))
                .map(Optional::get)
                .orElse(Try.failure(new NotFound404Exception(ExceptionMsgPattern.ENTITY_NOT_FOUND_BY_PARAM, User.class, "id", id)))
                .onFailure(th -> log.error(th.getMessage(), th));
    }

    public Try<User> updateRole(Long userId, String roleName) {
        Try<Role> role = this.fetchRole(roleName);
        if (role.isFailure())
            return Try.failure(new InternalError500Exception());
        return this.findById(userId)
                .map(user -> {
                    user.setRole(role.get());
                    return user;
                })
                .flatMap(user -> Try.of(() -> userRepository.save(user))
                        .onFailure(th -> log.error("Cannot update user role", th))
                        .recoverWith(th -> Try.failure(new InternalError500Exception()))
                );
    }

    public Page<User> findByUsernameOrEmailOrRole(
            String username, String email, String roleName, PageRequest pageRequest) {
        return userRepository.findByUsernameOrEmailOrRole(username, email, roleName, pageRequest);
    }

    public Try<List<Object[]>> findByIds(Set<Long> ids) {
        return Try.of(() -> userRepository.findAllByIds(ids))
                .filter(list -> list.size() == ids.size())
                .toTry(InternalError500Exception::new)
                .onFailure(th -> log.error("List of ids found differs from the provided ids", th));
    }
}
