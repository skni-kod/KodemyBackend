package pl.sknikod.kodemygateway.configuration;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import pl.sknikod.kodemygateway.infrastructure.module.oauth2.OAuth2ReactiveAuthorizationManager;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver.DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME;

@Configuration
@Slf4j
@EnableWebFluxSecurity
public class SecurityConfiguration {
    private static final Function<String, ServerWebExchangeMatcher> PATH_MATCHER_FUNCTION;

    static {
        PATH_MATCHER_FUNCTION = (endpoint) -> new PathPatternParserServerWebExchangeMatcher(
                endpoint + "/{" + DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME + "}"
        );
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver,
            @Value("${app.security.oauth2.endpoint.callback}") String callbackEndpoint,
            OAuth2ReactiveAuthorizationManager reactiveAuthenticationManager
    ) {
        http
                .authorizeExchange(auth -> auth.anyExchange().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .authorizationRequestResolver(authorizationRequestResolver)
                        .authenticationMatcher(callbackMatcher(callbackEndpoint))
                        .authenticationManager(reactiveAuthenticationManager)
                        .authenticationSuccessHandler(authenticationSuccessHandler())
                        // TODO check if need change
                        //.authenticationFailureHandler(authenticationFailureHandler())
                );
        return http.build();
    }

    private ServerAuthenticationSuccessHandler authenticationSuccessHandler() {
        return (webFilterExchange, authentication) -> webFilterExchange
                .getChain()
                .filter(webFilterExchange.getExchange())
                .and(Mono.empty());
    }

    /*private ServerAuthenticationFailureHandler authenticationFailureHandler() {
        return (webFilterExchange, exception) -> Mono.empty();
    }*/

    @Bean
    public ServerOAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            @Value("${app.security.oauth2.endpoint.authorize}") String authorizeEndpoint
    ) {
        return new DefaultServerOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, PATH_MATCHER_FUNCTION.apply(authorizeEndpoint)
        );
    }

    public ServerWebExchangeMatcher callbackMatcher(@NonNull String callbackEndpoint) {
        return PATH_MATCHER_FUNCTION.apply(callbackEndpoint);
    }
}
