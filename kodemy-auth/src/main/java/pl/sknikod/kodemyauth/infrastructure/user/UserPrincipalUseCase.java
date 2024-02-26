package pl.sknikod.kodemyauth.infrastructure.user;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.configuration.SecurityConfig;
import pl.sknikod.kodemyauth.exception.structure.OAuth2Exception;
import pl.sknikod.kodemyauth.infrastructure.auth.oauth2.OAuth2UserInfo;
import pl.sknikod.kodemyauth.infrastructure.auth.oauth2.OAuth2UserInfoFactory;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Provider;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Role;
import pl.sknikod.kodemyauth.infrastructure.common.entity.User;
import pl.sknikod.kodemyauth.infrastructure.common.entity.UserPrincipal;
import pl.sknikod.kodemyauth.infrastructure.common.repository.RoleRepository;
import pl.sknikod.kodemyauth.infrastructure.common.repository.UserRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@AllArgsConstructor
public class UserPrincipalUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SecurityConfig.SecurityProperties.RoleProperties roleProperties;

    public UserPrincipal execute(OAuth2User oAuth2User, String registrationId) {
        OAuth2UserInfo authUserInfo = OAuth2UserInfoFactory.getAuthUserInfo(
                registrationId, oAuth2User.getAttributes()
        );
        return Option.of(userRepository.findUserByPrincipalIdAndAuthProviderWithFetchRole(
                        authUserInfo.getPrincipalId(), authUserInfo.getProvider()
                ))
                .fold(() -> this.create(authUserInfo),
                        user -> this.create(user, authUserInfo.getAttributes()));
    }

    public UserPrincipal create(User user, Map<String, Object> attributes) {
        return Option
                .of(user.getRole().getName())
                .map(roleProperties::getPrivileges)
                .map(authorities -> UserPrincipal.create(user, authorities, attributes))
                .getOrElse(() -> UserPrincipal.create(user, new HashSet<>(), attributes));
    }

    public UserPrincipal create(OAuth2UserInfo authUserInfo) {
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
                .map(user -> this.create(user, authUserInfo.getAttributes()))
                .getOrElseThrow(
                        () -> new OAuth2Exception("Failed to user principal processing")
                );
    }
}
