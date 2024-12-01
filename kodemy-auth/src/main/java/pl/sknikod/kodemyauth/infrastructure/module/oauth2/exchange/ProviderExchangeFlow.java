package pl.sknikod.kodemyauth.infrastructure.module.oauth2.exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver.DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME;

@RequiredArgsConstructor
@Slf4j
public abstract class ProviderExchangeFlow {
    protected final RestTemplate restTemplate;
    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_MAP_TYPE;

    static {
        PARAMETERIZED_MAP_TYPE = new ParameterizedTypeReference<>() {
        };
    }

    public abstract Registration getRegistration();

    public abstract boolean isApply(String registrationId);

    public abstract ProviderUser exchange(ClientRegistrationRepository repository, String code);

    protected Map<String, Object> initNewAttributesMap(ClientRegistration clientRegistration) {
        return new HashMap<>(Map.of(DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME, clientRegistration.getRegistrationId()));
    }

    protected AccessToken postForAccessToken(ClientRegistration clientRegistration, @NonNull String code) {
        var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        Map<String, String> params = Map.of(
                "client_id", clientRegistration.getClientId(),
                "client_secret", clientRegistration.getClientSecret(),
                "code", code,
                "grant_type", "authorization_code"
        );
        return restTemplate.postForObject(
                clientRegistration.getProviderDetails().getTokenUri(), new HttpEntity<>(params, headers), AccessToken.class);
    }

    protected Map<String, Object> getUserAttributes(ClientRegistration clientRegistration, AccessToken accessToken) {
        var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(accessToken.accessToken);
        String userInfoUri = clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri();
        return restTemplate.exchange(userInfoUri, HttpMethod.GET, new HttpEntity<>(headers), PARAMETERIZED_MAP_TYPE).getBody();
    }

    @Data
    protected final static class AccessToken {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("token_type")
        private String tokenType;
    }
}
