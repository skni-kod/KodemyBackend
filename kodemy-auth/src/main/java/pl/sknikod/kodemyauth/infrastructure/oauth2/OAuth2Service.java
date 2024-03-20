package pl.sknikod.kodemyauth.infrastructure.oauth2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.sknikod.kodemyauth.exception.structure.ServerProcessingException;
import pl.sknikod.kodemyauth.infrastructure.AuthDetails;
import pl.sknikod.kodemyauth.infrastructure.oauth2.userinfo.GithubOAuth2UserInfo;
import pl.sknikod.kodemyauth.infrastructure.token.TokenUseCase;
import pl.sknikod.kodemyauth.infrastructure.user.UserPrincipalUseCase;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2Service implements OAuth2AuthorizedClientService {

    private final UserPrincipalUseCase userPrincipalUseCase;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final TokenUseCase tokenUseCase;

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        return null;
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient client, Authentication authentication) {
        String provider = client.getClientRegistration().getRegistrationId().toUpperCase();
        var attributes = ((OAuth2User) authentication.getPrincipal()).getAttributes();
        var oAuth2UserInfo = switch (provider) {
            case "GITHUB":
                String accessToken = client.getAccessToken().getTokenValue();
                Map<String, Object> modifiableMap = new HashMap<>(attributes);
                modifiableMap.put("email", fetchGitHubEmail(accessToken).orElse(Strings.EMPTY));
                yield new GithubOAuth2UserInfo(modifiableMap);
            default:
                throw new ServerProcessingException("Unsupported OAuth2 provider: " + provider);
        };

        var tokens = tokenUseCase.generate(userPrincipalUseCase.execute(oAuth2UserInfo));
        var details = authentication.getDetails();
        var authDetails = (details instanceof AuthDetails) ? (AuthDetails) details : new AuthDetails();
        authDetails.setAccessToken(tokens.getAccessToken());
        authDetails.setRefreshToken(tokens.getRefreshToken());
        ((OAuth2AuthenticationToken) authentication).setDetails(authDetails);
        log.info(String.format("Authorized client: %s", provider));
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
    }

    private Optional<String> fetchGitHubEmail(String accessToken) {
        String apiUrl = "https://api.github.com/user/emails";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        if (response.getStatusCode() == HttpStatus.OK && response.hasBody()) {
            return Try.of(() -> objectMapper.readTree(response.getBody()))
                    .map(jsonArray -> {
                        for (JsonNode emailObject : jsonArray) {
                            if (emailObject.get("primary").asBoolean())
                                return emailObject.get("email").asText();
                        }
                        return null;
                    })
                    .toJavaOptional();
        }
        return Optional.empty();
    }
}