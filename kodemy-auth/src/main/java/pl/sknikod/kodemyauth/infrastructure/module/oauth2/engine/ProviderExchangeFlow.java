package pl.sknikod.kodemyauth.infrastructure.module.oauth2.engine;

import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestTemplate;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;

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
        log.info("Exchanging {}'s authorization code", clientRegistration.getRegistrationId());
        var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        Map<String, String> params = Map.of(
                "client_id", clientRegistration.getClientId(),
                "client_secret", clientRegistration.getClientSecret(),
                "code", code,
                "grant_type", "authorization_code"
        );
        return Try.of(() -> restTemplate.exchange(
                        clientRegistration.getProviderDetails().getTokenUri(),
                        HttpMethod.POST, new HttpEntity<>(params, headers), PARAMETERIZED_MAP_TYPE
                ))
                .onFailure(th -> log.error("Error during the exchange of authorization code", th))
                .filter(res -> {
                    if (res.getBody() != null && res.getBody().containsKey("error")) {
                        log.error("Error during the exchange of authorization code: {}", res.getBody());
                        return false;
                    }
                    return true;
                })
                .toTry(() -> new InternalError500Exception("Failed to exchange authorization code"))
                .map(HttpEntity::getBody)
                .map(body -> new AccessToken(
                        (String) body.getOrDefault("access_token", null),
                        (String) body.getOrDefault("token_type", null)
                ))
                .onSuccess(accessToken -> log.info(accessToken.toString()))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    protected Map<String, Object> getUserAttributes(ClientRegistration clientRegistration, AccessToken accessToken) {
        log.info("Fetching {}'s user attributes", clientRegistration.getRegistrationId());
        var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        log.info("Access {}", accessToken.toString());
        headers.setBearerAuth(accessToken.accessToken);

        return Try.of(() -> restTemplate.exchange(
                        clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri(),
                        HttpMethod.GET, new HttpEntity<>(headers), PARAMETERIZED_MAP_TYPE
                ))
                .onFailure(th -> log.error("Error during fetching user attributes", th))
                .toTry(() -> new InternalError500Exception("Failed to fetch user attributes"))
                .map(HttpEntity::getBody)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    @Value
    protected static class AccessToken {
        String accessToken;
        String tokenType;
    }
}
