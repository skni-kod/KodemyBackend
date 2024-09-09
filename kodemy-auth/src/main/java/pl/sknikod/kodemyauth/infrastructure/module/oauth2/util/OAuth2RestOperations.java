package pl.sknikod.kodemyauth.infrastructure.module.oauth2.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class OAuth2RestOperations {
    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestTemplate oAuth2RestTemplate;

    public RequestEntity<?> map(@NonNull String url, @NonNull OAuth2UserRequest userRequest) {
        URI uri = UriComponentsBuilder.fromUriString(url)
                .build().toUri();
        final var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(userRequest.getAccessToken().getTokenValue());
        return new RequestEntity<>(headers, HttpMethod.GET, uri);
    }


    public ResponseEntity<Map<String, Object>> exchange(@NonNull OAuth2UserRequest userRequest) {
        String userInfoUri = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUri();
        return this.oAuth2RestTemplate.exchange(map(userInfoUri, userRequest), PARAMETERIZED_RESPONSE_TYPE);
    }

    public <T> ResponseEntity<T> exchange(
            @NonNull String url,
            @NonNull Class<T> responseType,
            @NonNull OAuth2UserRequest userRequest
    ) {
        return this.oAuth2RestTemplate.exchange(map(url, userRequest), responseType);
    }

    public <T> ResponseEntity<T> exchange(
            @NonNull String url,
            @NonNull ParameterizedTypeReference<T> responseType,
            @NonNull OAuth2UserRequest userRequest
    ) {
        return this.oAuth2RestTemplate.exchange(map(url, userRequest), responseType);
    }
}
