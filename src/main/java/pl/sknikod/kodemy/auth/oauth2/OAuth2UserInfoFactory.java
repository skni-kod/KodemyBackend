package pl.sknikod.kodemy.auth.oauth2;

import pl.sknikod.kodemy.exception.OAuth2AuthenticationProcessingException;
import pl.sknikod.kodemy.user.UserProviderType;
import pl.sknikod.kodemy.util.ExceptionMessage;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getAuthUserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(UserProviderType.github.name()))
            return new GithubOAuth2UserInfo(attributes);
        throw new OAuth2AuthenticationProcessingException(ExceptionMessage.PROVIDER_NOT_SUPPORT.message);
    }
}