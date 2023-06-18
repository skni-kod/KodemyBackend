package pl.sknikod.kodemy.infrastructure.auth.oauth2;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.sknikod.kodemy.exception.origin.OAuth2AuthenticationProcessingException;
import pl.sknikod.kodemy.infrastructure.model.user.UserProviderType;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getAuthUserInfo(UserProviderType providerType, Map<String, Object> attributes) {
        return switch (providerType) {
            case GITHUB -> new GithubOAuth2UserInfo(attributes);
            default ->
                    throw new OAuth2AuthenticationProcessingException("The OAuth2 provider used is not supported yet.");
        };
    }
}