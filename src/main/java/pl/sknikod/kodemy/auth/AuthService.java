package pl.sknikod.kodemy.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.auth.oauth2.OAuth2UserInfo;
import pl.sknikod.kodemy.auth.oauth2.OAuth2UserInfoFactory;
import pl.sknikod.kodemy.provider.Provider;
import pl.sknikod.kodemy.provider.ProviderRepository;
import pl.sknikod.kodemy.role.Role;
import pl.sknikod.kodemy.role.RoleRepository;
import pl.sknikod.kodemy.user.User;
import pl.sknikod.kodemy.user.UserProviderType;
import pl.sknikod.kodemy.user.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    public static final String DEFAULT_USER_ROLE_NAME = "ROLE_USER";
    private final ProviderRepository providerRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        User user;
        try {
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            UserProviderType providerType = UserProviderType.valueOf(registrationId);

            OAuth2UserInfo authUserInfo = OAuth2UserInfoFactory.getAuthUserInfo(
                    registrationId,
                    oAuth2User.getAttributes()
            );
            user = userRepository.findUserByPrincipalIdAndAuthProvider(
                    authUserInfo.getPrincipalId(),
                    providerType
            );
            if (user == null)
                user = createUser(authUserInfo, providerType);
            else
                user.setAttributes(authUserInfo.attributes);
            return user;
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private User createUser(OAuth2UserInfo authUserInfo, UserProviderType providerType) {
        Role role = roleRepository.findByName(DEFAULT_USER_ROLE_NAME)
                .orElseGet(
                        () -> roleRepository.save(new Role(DEFAULT_USER_ROLE_NAME))
                );
        User user = userRepository.save(new User(
                authUserInfo.getUsername(),
                authUserInfo.getEmail(),
                authUserInfo.getPhoto(),
                authUserInfo.attributes,
                Set.of(role)
        ));
        providerRepository.save(new Provider(
                authUserInfo.getPrincipalId(),
                providerType,
                authUserInfo.getEmail(),
                authUserInfo.getPhoto(),
                user
        ));
        return user;
    }

    public Map<String, String> getUserInfo(OAuth2AuthenticationToken authToken) {
        return Map.of(
                "name", authToken.getName(),
                "authorities", authToken.getAuthorities().toString()
        );
    }
}