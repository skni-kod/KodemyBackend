package pl.sknikod.kodemyauth.infrastructure.oauth2.userinfo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.sknikod.kodemyauth.exception.structure.OAuth2Exception;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo createOAuth2UserInfo(String registrationName, Map<String, Object> attributes) {
        return switch (registrationName.toUpperCase()) {
            case "GITHUB" -> new GithubOAuth2UserInfo(attributes);
            default -> throw new OAuth2Exception("The OAuth2 provider used is not supported yet.");
        };
    }
}