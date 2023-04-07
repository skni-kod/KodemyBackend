package pl.sknikod.kodemy.auth;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.auth.oauth2.OAuth2UserInfo;
import pl.sknikod.kodemy.auth.oauth2.OAuth2UserInfoFactory;
import pl.sknikod.kodemy.config.AppConfig;
import pl.sknikod.kodemy.exception.origin.OAuth2AuthenticationProcessingException;
import pl.sknikod.kodemy.provider.Provider;
import pl.sknikod.kodemy.provider.ProviderRepository;
import pl.sknikod.kodemy.rest.mapper.UserMapper;
import pl.sknikod.kodemy.rest.response.UserOAuth2MeResponse;
import pl.sknikod.kodemy.role.RoleRepository;
import pl.sknikod.kodemy.user.User;
import pl.sknikod.kodemy.user.UserPrincipal;
import pl.sknikod.kodemy.user.UserProviderType;
import pl.sknikod.kodemy.user.UserRepository;

import java.util.HashSet;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
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
                .of(UserPrincipal.getCurrentSessionUser())
                .map(UserPrincipal::getId)
                .map(userRepository::findById)
                .map(userOptional -> Option.ofOptional(userOptional).getOrNull())
                .map(userMapper::map)
                .getOrNull();
    }

    @Component
    @AllArgsConstructor
    private static class UserPrincipalUseCase{
        private RoleRepository roleRepository;
        private AppConfig.SecurityRoleProperties roleProperties;
        private final UserRepository userRepository;
        private final ProviderRepository providerRepository;

        public UserPrincipal execute(OAuth2User oAuth2User, String registrationId){
            UserProviderType providerType = Option.of(registrationId)
                    .map(UserProviderType::valueOf)
                    .getOrNull();
            OAuth2UserInfo authUserInfo = OAuth2UserInfoFactory.getAuthUserInfo(
                    providerType, oAuth2User.getAttributes()
            );
            return Option.of(userRepository.findUserByPrincipalIdAndAuthProvider(
                            authUserInfo.getPrincipalId(), providerType
                    ))
                    .map(user -> this.create(user, authUserInfo.attributes))
                    .getOrElse(() -> this.create(authUserInfo, providerType));
        }

        public UserPrincipal create(User user, Map<String, Object> attributes){
            return Option
                    .of(user.getRole().getName())
                    .map(roleProperties::getPrivileges)
                    .map(authorities -> UserPrincipal.create(user, authorities, attributes))
                    .getOrElse(() -> UserPrincipal.create(user, new HashSet<>(), attributes));
        }
        public UserPrincipal create(OAuth2UserInfo authUserInfo, UserProviderType providerType) {
            return Try.of(roleProperties::getDefaultRole)
                    .map(roleRepository::findByName)
                    .map(role -> new User(
                            authUserInfo.getUsername(),
                            authUserInfo.getEmail(),
                            authUserInfo.getPhoto(),
                            role
                    ))
                    .map(userRepository::save)
                    .map(user -> {
                        providerRepository.save(new Provider(
                                authUserInfo.getPrincipalId(),
                                providerType,
                                authUserInfo.getEmail(),
                                authUserInfo.getPhoto(),
                                user));
                        return user;
                    })
                    .map(user -> this.create(user, authUserInfo.attributes))
                    .getOrElseThrow(
                            () -> new OAuth2AuthenticationProcessingException("Failed to user principal processing")
                    );
        }
    }
}