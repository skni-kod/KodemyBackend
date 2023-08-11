package pl.sknikod.kodemy.infrastructure.rest;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.configuration.AppConfig;
import pl.sknikod.kodemy.exception.origin.OAuth2AuthenticationProcessingException;
import pl.sknikod.kodemy.exception.structure.NotFoundException;
import pl.sknikod.kodemy.infrastructure.auth.oauth2.OAuth2UserInfo;
import pl.sknikod.kodemy.infrastructure.auth.oauth2.OAuth2UserInfoFactory;
import pl.sknikod.kodemy.infrastructure.model.entity.*;
import pl.sknikod.kodemy.infrastructure.model.repository.RoleRepository;
import pl.sknikod.kodemy.infrastructure.model.repository.UserRepository;
import pl.sknikod.kodemy.infrastructure.rest.model.UserOAuth2MeResponse;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final AuthMapper authMapper;
    private final UserPrincipalUseCase userPrincipalUseCase;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            return userPrincipalUseCase.execute(super.loadUser(userRequest), userRequest.getClientRegistration().getRegistrationId());
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    public UserOAuth2MeResponse getUserInfo() {
        return Option
                .of(UserService.getContextUserPrincipal())
                .map(UserPrincipal::getId)
                .map(userRepository::findById)
                .map(userOptional -> Option.ofOptional(userOptional).getOrNull())
                .map(authMapper::map)
                .getOrNull();
    }

    @Component
    @AllArgsConstructor
    private static class UserPrincipalUseCase {
        private final UserRepository userRepository;
        private RoleRepository roleRepository;
        private AppConfig.SecurityRoleProperties roleProperties;

        public UserPrincipal execute(OAuth2User oAuth2User, String registrationId) {
            UserProviderType providerType = Option.of(registrationId)
                    .map(String::toUpperCase)
                    .map(UserProviderType::valueOf)
                    .getOrNull();
            OAuth2UserInfo authUserInfo = OAuth2UserInfoFactory.getAuthUserInfo(
                    providerType, oAuth2User.getAttributes()
            );
            return Option.of(userRepository.findUserByPrincipalIdAndAuthProvider(
                            authUserInfo.getPrincipalId(), providerType
                    ))
                    .map(user -> this.create(user, authUserInfo.getAttributes()))
                    .getOrElse(() -> this.create(authUserInfo, providerType));
        }

        public UserPrincipal create(User user, Map<String, Object> attributes) {
            return Option
                    .of(user.getRole().getName())
                    .map(roleProperties::getPrivileges)
                    .map(authorities -> UserPrincipal.create(user, authorities, attributes))
                    .getOrElse(() -> UserPrincipal.create(user, new HashSet<>(), attributes));
        }

        public UserPrincipal create(OAuth2UserInfo authUserInfo, UserProviderType providerType) {
            return Try.of(roleProperties::getDefaultRole)
                    .map(RoleName::valueOf)
                    .map(roleRepository::findByName)
                    .map(Optional::get)
                    .map(role -> new User(
                            authUserInfo.getUsername(), authUserInfo.getEmail(),
                            authUserInfo.getPhoto(), role
                    ))
                    .peek(user -> user.setProviders(Set.of(new Provider(
                            authUserInfo.getPrincipalId(), providerType,
                            authUserInfo.getEmail(), authUserInfo.getPhoto(), user
                    ))))
                    .map(userRepository::save)
                    .map(user -> this.create(user, authUserInfo.getAttributes()))
                    .getOrElseThrow(
                            () -> new OAuth2AuthenticationProcessingException("Failed to user principal processing")
                    );
        }
    }

    @Mapper(componentModel = "spring")
    public interface AuthMapper {
        UserOAuth2MeResponse map(User user);
    }
}