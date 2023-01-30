package pl.sknikod.kodemy.auth.oauth2;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.user.User;
import pl.sknikod.kodemy.user.UserProviderType;
import pl.sknikod.kodemy.user.UserRepository;

@Service
@AllArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        User user;
        try {
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            OAuth2UserInfo authUserInfo = OAuth2UserInfoFactory.getAuthUserInfo(
                    registrationId,
                    oAuth2User.getAttributes()
            );
            user = userRepository.findUserByPrincipalIdAndAuthProvider(
                    authUserInfo.getPrincipalId(),
                    UserProviderType.valueOf(registrationId)
            );
            if (user == null)
                user = createUser(authUserInfo, UserProviderType.valueOf(registrationId));
            return user;
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private User createUser(OAuth2UserInfo authUserInfo, UserProviderType authProvider) {
        User user = new User.UserBuilder(
                authUserInfo.getUsername(),
                authUserInfo.getEmail(),
                authUserInfo.getPhoto(),
                authUserInfo.attributes,
                authUserInfo.getPrincipalId(),
                authProvider
        ).build();
        return userRepository.save(user);
    }
}