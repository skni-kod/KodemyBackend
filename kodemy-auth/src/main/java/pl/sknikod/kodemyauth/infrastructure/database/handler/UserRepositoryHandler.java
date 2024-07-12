package pl.sknikod.kodemyauth.infrastructure.database.handler;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.configuration.SecurityConfig;
import pl.sknikod.kodemyauth.exception.ExceptionPattern;
import pl.sknikod.kodemyauth.exception.structure.NotFoundException;
import pl.sknikod.kodemyauth.exception.structure.ServerProcessingException;
import pl.sknikod.kodemyauth.infrastructure.database.entity.Provider;
import pl.sknikod.kodemyauth.infrastructure.database.entity.Role;
import pl.sknikod.kodemyauth.infrastructure.database.entity.User;
import pl.sknikod.kodemyauth.infrastructure.database.repository.RoleRepository;
import pl.sknikod.kodemyauth.infrastructure.database.repository.UserRepository;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.OAuth2Provider;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryHandler {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SecurityConfig.RoleProperties roleProperties;

    public Optional<User> save(OAuth2Provider.User providerUser) {
        return this.fetchRole(Role.RoleName.valueOf(roleProperties.getPrimary()))
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

    private Try<Role> fetchRole(Role.RoleName roleName) {
        return Try.of(() -> roleRepository.findByName(roleName))
                .map(Optional::get)
                .toTry(() -> new NotFoundException(ExceptionPattern.ENTITY_NOT_FOUND, Role.class))
                .onFailure(th -> log.error(th.getMessage()));
    }

    public Try<User> findByProviderUser(OAuth2Provider.User providerUser) {
        return Option.of(userRepository.findUserByPrincipalIdAndAuthProviderWithFetchRole(
                        providerUser.getPrincipalId(), providerUser.getProvider()))
                .toTry(() -> new NotFoundException(ExceptionPattern.ENTITY_NOT_FOUND, User.class))
                .onFailure(th -> log.error(th.getMessage(), th));
    }

    public Try<User> findById(Long id) {
        return Try.of(() -> userRepository.findById(id))
                .map(Optional::get)
                .orElse(Try.failure(new NotFoundException(ExceptionPattern.ENTITY_NOT_FOUND_BY_PARAM, User.class, "id", id)))
                .onFailure(th -> log.error(th.getMessage(), th));
    }

    public Try<User> updateRole(Long userId, Role.RoleName roleName) {
        Try<Role> role = this.fetchRole(roleName);
        if (role.isFailure())
            return Try.failure(new ServerProcessingException());
        return this.findById(userId)
                .map(user -> {
                    user.setRole(role.get());
                    return user;
                })
                .flatMap(user -> Try.of(() -> userRepository.save(user))
                        .onFailure(th -> log.error("Cannot update user role", th))
                        .recoverWith(th -> Try.failure(new ServerProcessingException()))
                );
    }

    public Page<User> findByUsernameOrEmailOrRole(
            String username, String email, Role role, PageRequest pageRequest) {
        return userRepository.findByUsernameOrEmailOrRole(username, email, role, pageRequest);
    }

    public Try<List<Object[]>> findByIds(Set<Long> ids) {
        return Try.of(() -> userRepository.findAllByIds(ids))
                .filter(list -> list.size() == ids.size())
                .toTry(ServerProcessingException::new)
                .onFailure(th -> log.error("List of ids found differs from the provided ids", th));
    }
}
