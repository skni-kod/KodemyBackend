package pl.sknikod.kodemy.infrastructure.auth.oauth2;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.sknikod.kodemy.exception.structure.OAuth2Exception;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getAuthUserInfo(String registrationName, Map<String, Object> attributes) {
        return switch (registrationName.toUpperCase()) {
            case "GITHUB" -> new GithubOAuth2UserInfo(attributes);
            default -> throw new OAuth2Exception("The OAuth2 provider used is not supported yet.");
        };
    }
}