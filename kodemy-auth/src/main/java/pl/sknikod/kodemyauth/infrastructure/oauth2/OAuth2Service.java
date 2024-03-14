package pl.sknikod.kodemyauth.infrastructure.oauth2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.common.entity.UserPrincipal;
import pl.sknikod.kodemyauth.infrastructure.oauth2.userinfo.OAuth2UserInfo;
import pl.sknikod.kodemyauth.infrastructure.oauth2.userinfo.OAuth2UserInfoFactory;
import pl.sknikod.kodemyauth.infrastructure.user.UserPrincipalUseCase;
import pl.sknikod.kodemyauth.util.JwtUtil;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2Service implements OAuth2AuthorizedClientService {

    private final UserPrincipalUseCase userPrincipalUseCase;
    private final JwtUtil jwtUtil;

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        return null;
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient client, Authentication authentication) {
        String registrationId = client.getClientRegistration().getRegistrationId();
        OAuth2UserInfo authUserInfo = OAuth2UserInfoFactory.createOAuth2UserInfo(
                registrationId,
                ((OAuth2User) authentication.getPrincipal()).getAttributes()
        );
        UserPrincipal userPrincipal = userPrincipalUseCase.execute(authUserInfo);
        JwtUtil.Output output = jwtUtil.generateToken(new JwtUtil.Input(
                userPrincipal.getId(), userPrincipal.getUsername(), userPrincipal.getAuthorities()));
        ((OAuth2AuthenticationToken) authentication).setDetails(output.getBearer());
        log.info(String.format("Saved authorized client: %s", registrationId));
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
    }
}