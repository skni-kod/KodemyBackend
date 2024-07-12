package pl.sknikod.kodemygateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.config.GlobalCorsProperties;
import org.springframework.cloud.gateway.handler.FilteringWebHandler;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.web.reactive.DispatcherHandler;
import pl.sknikod.kodemygateway.configuration.properties.DatabusProperties;
import pl.sknikod.kodemygateway.infrastructure.module.oauth2.OAuth2AuthorizationReqResolver;
import pl.sknikod.kodemygateway.infrastructure.module.oauth2.OAuth2ReactiveAuthorizationManager;
import pl.sknikod.kodemygateway.infrastructure.module.oauth2.handler.OAuth2AuthorizationFailureHandler;
import pl.sknikod.kodemygateway.infrastructure.module.oauth2.handler.OAuth2AuthorizationSuccessHandler;
import pl.sknikod.kodemygateway.infrastructure.module.oauth2.util.route.GatewayRedirectStrategy;
import pl.sknikod.kodemygateway.infrastructure.module.oauth2.util.route.GatewayRouteHandlerMapping;

import static org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver.DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    public static final String OAUTH2_PROVIDER_SUFFIX = "/{" + DEFAULT_REGISTRATION_ID_URI_VARIABLE_NAME + "}";

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            @Value("${spring.security.oauth2.path.authorize}") String oAuth2AuthorizeUri,
            @Value("${spring.security.oauth2.path.callback}") String oAuth2CallbackUri,
            OAuth2ReactiveAuthorizationManager oAuth2AuthorizationManager,
            OAuth2AuthorizationSuccessHandler oAuth2AuthorizationSuccessHandler,
            OAuth2AuthorizationFailureHandler oAuth2AuthorizationFailureHandler
    ) {
        final var oAuth2AuthorizeResolver = new OAuth2AuthorizationReqResolver(
                clientRegistrationRepository, oAuth2AuthorizeUri + OAUTH2_PROVIDER_SUFFIX
        );
        final var oAuth2CallbackMatcher =
                new PathPatternParserServerWebExchangeMatcher(oAuth2CallbackUri + OAUTH2_PROVIDER_SUFFIX);

        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .oauth2Login(login -> login
                        .authorizationRequestResolver(oAuth2AuthorizeResolver)
                        .authenticationMatcher(oAuth2CallbackMatcher)
                        .authenticationManager(oAuth2AuthorizationManager)
                        .authenticationSuccessHandler(oAuth2AuthorizationSuccessHandler)
                        .authenticationFailureHandler(oAuth2AuthorizationFailureHandler)
                );
        return http.build();
    }

    @Bean
    public GatewayRouteHandlerMapping gatewayRouteHandlerMapping(
            FilteringWebHandler webHandler, RouteLocator routeLocator,
            GlobalCorsProperties globalCorsProperties, Environment environment
    ){
        return new GatewayRouteHandlerMapping(webHandler, routeLocator, globalCorsProperties, environment);
    }

    @Bean
    public OAuth2AuthorizationSuccessHandler oAuth2AuthorizationSuccessHandler(
            DispatcherHandler dispatcherHandler, DatabusProperties databusProperties
    ) {
        return new OAuth2AuthorizationSuccessHandler(dispatcherHandler, databusProperties);
    }

    @Bean
    public OAuth2AuthorizationFailureHandler oAuth2AuthorizationFailureHandler(
            GatewayRedirectStrategy gatewayRedirectStrategy,
            @Value("${network.routes.auth}") String frontRouteBaseUrl,
            @Value("${spring.security.oauth2.path.error}") String errorPath
    ) {
        return new OAuth2AuthorizationFailureHandler(
                gatewayRedirectStrategy, frontRouteBaseUrl, errorPath);
    }
}
