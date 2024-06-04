package pl.sknikod.kodemygateway.infrastructure.module.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class OAuth2AuthorizationReqResolver implements ServerOAuth2AuthorizationRequestResolver {
    private final DefaultServerOAuth2AuthorizationRequestResolver delegate;

    public OAuth2AuthorizationReqResolver(
            ReactiveClientRegistrationRepository clientRegistrationRepository, String baseUri
    ) {
        PathPatternParserServerWebExchangeMatcher authorizationRequestMatcher = new PathPatternParserServerWebExchangeMatcher(baseUri);
        this.delegate = new DefaultServerOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, authorizationRequestMatcher);
    }

    @Override
    public Mono<OAuth2AuthorizationRequest> resolve(ServerWebExchange exchange) {
        return delegate.resolve(exchange);
    }

    @Override
    public Mono<OAuth2AuthorizationRequest> resolve(ServerWebExchange exchange, String clientRegistrationId) {
        return delegate.resolve(exchange, clientRegistrationId);
    }
}
