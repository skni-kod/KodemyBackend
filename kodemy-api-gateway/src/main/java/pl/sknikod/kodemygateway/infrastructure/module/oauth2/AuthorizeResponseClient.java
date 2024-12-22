package pl.sknikod.kodemygateway.infrastructure.module.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import pl.sknikod.kodemygateway.infrastructure.module.oauth2.model.AuthorizeResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

@Component
public class AuthorizeResponseClient {
    private final WebClient webClient = WebClient.builder().build();
    private final AuthorizeResponseBodyExtractor bodyExtractor = new AuthorizeResponseBodyExtractor();
    private final Function<String, String> authorizeUriFunction;

    public AuthorizeResponseClient(
            @Value("${service.baseUrl.auth}") String authBaseUrl,
            @Value("${app.security.oauth2.endpoint.authorize}") String authorizeEndpoint) {
        this.authorizeUriFunction = (registrationId) -> authBaseUrl + authorizeEndpoint + "/" + registrationId;
    }

    public Mono<AuthorizeResponse> getAuthorizeResponse(
            ClientRegistration clientRegistration, OAuth2AuthorizationExchange authorizationExchange) {
        URI uri = UriComponentsBuilder.fromUriString(authorizeUriFunction.apply(clientRegistration.getRegistrationId()))
                .queryParam("code", authorizationExchange.getAuthorizationResponse().getCode())
                .build().toUri();
        return this.webClient.get().uri(uri).exchangeToMono(clientResponse -> clientResponse.body(this.bodyExtractor));
    }

    public static class AuthorizeResponseBodyExtractor implements BodyExtractor<Mono<AuthorizeResponse>, ReactiveHttpInputMessage> {
        private static final ParameterizedTypeReference<Map<String, Object>> STRING_OBJECT_MAP;

        static {
            STRING_OBJECT_MAP = new ParameterizedTypeReference<>() {
            };
        }

        @Override
        public @NonNull Mono<AuthorizeResponse> extract(@NonNull ReactiveHttpInputMessage inputMessage, @NonNull Context context) {
            return BodyExtractors.toMono(STRING_OBJECT_MAP)
                    .extract(inputMessage, context)
                    .onErrorMap((ex) -> new OAuth2AuthorizationException(new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR)))
                    .switchIfEmpty(Mono.error(() -> new OAuth2AuthorizationException(new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR))))
                    .flatMap(this::parse);
        }

        private Mono<AuthorizeResponse> parse(Map<String, Object> map) {
            try {
                return Mono.just(AuthorizeResponse.fromMap(map));
            } catch (IllegalArgumentException | NullPointerException ex) {
                OAuth2Error oauth2Error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR);
                return Mono.error(new OAuth2AuthorizationException(oauth2Error, ex));
            }
        }
    }
}
