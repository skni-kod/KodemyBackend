package pl.sknikod.kodemyauth.infrastructure.user;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.configuration.SecurityConfig;
import pl.sknikod.kodemyauth.exception.structure.OAuth2Exception;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Provider;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Role;
import pl.sknikod.kodemyauth.infrastructure.common.entity.User;
import pl.sknikod.kodemyauth.infrastructure.common.entity.UserPrincipal;
import pl.sknikod.kodemyauth.infrastructure.common.repository.RoleRepository;
import pl.sknikod.kodemyauth.infrastructure.common.repository.UserRepository;
import pl.sknikod.kodemyauth.infrastructure.oauth2.userinfo.OAuth2UserInfo;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@AllArgsConstructor
public class UserPrincipalUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SecurityConfig.SecurityProperties.RoleProperties roleProperties;

    public UserPrincipal execute(OAuth2UserInfo userInfo) {
        return Option.of(userRepository.findUserByPrincipalIdAndAuthProviderWithFetchRole(
                        userInfo.getPrincipalId(), userInfo.getProvider()
                ))
                .fold(() -> this.create(userInfo), this::create);
    }
    private UserPrincipal create(OAuth2UserInfo authUserInfo) {
        return Try.of(roleProperties::getDefaultRole)
                .map(Role.RoleName::valueOf)
                .map(roleRepository::findByName)
                .map(Optional::get)
                .map(role -> new User(
                        authUserInfo.getUsername(), authUserInfo.getEmail(),
                        authUserInfo.getPhoto(), role
                ))
                .peek(user -> user.setProviders(Set.of(new Provider(
                        authUserInfo.getPrincipalId(), authUserInfo.getProvider(),
                        authUserInfo.getEmail(), authUserInfo.getPhoto(), user
                ))))
                .map(userRepository::save)
                .map(this::create)
                .getOrElseThrow(
                        () -> new OAuth2Exception("Failed to user principal processing")
                );
    }

    private UserPrincipal create(User user) {
        return Option
                .of(user.getRole().getName())
                .map(roleProperties::getPrivileges)
                .fold(
                        () -> UserPrincipal.create(user, new HashSet<>()),
                        authorities -> UserPrincipal.create(user, authorities)
                );
    }
}
