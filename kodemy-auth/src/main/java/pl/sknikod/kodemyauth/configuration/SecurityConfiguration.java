package pl.sknikod.kodemyauth.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.dao.RefreshTokenDao;
import pl.sknikod.kodemyauth.infrastructure.module.auth.LogoutService;
import pl.sknikod.kodemyauth.infrastructure.module.auth.handler.LogoutRequestHandler;
import pl.sknikod.kodemyauth.infrastructure.module.auth.handler.LogoutSuccessHandler;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.OAuth2AuthorizationRequestRepository;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.OAuth2Service;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.handler.OAuth2LoginFailureHandler;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.handler.OAuth2LoginSuccessHandler;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2Constant;
import pl.sknikod.kodemyauth.util.route.RouteRedirectStrategy;
import pl.sknikod.kodemycommons.exception.handler.ServletExceptionHandler;
import pl.sknikod.kodemycommons.security.JwtAuthorizationFilter;
import pl.sknikod.kodemycommons.security.JwtProvider;
import pl.sknikod.kodemycommons.security.configuration.JwtConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true)
@RequiredArgsConstructor
@Import({JwtConfiguration.class})
@EnableAutoConfiguration(exclude = UserDetailsServiceAutoConfiguration.class)
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthorizationFilter jwtAuthorizationFilter,
            OAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository,
            OAuth2EndpointsProperties oAuth2EndpointsProperties,
            OAuth2Service oAuth2Service,
            OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
            OAuth2LoginFailureHandler oAuth2LoginFailureHandler,
            ServletExceptionHandler servletExceptionHandler,
            LogoutRequestHandler logoutRequestHandler,
            LogoutSuccessHandler logoutSuccessHandler
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(autz -> autz.anyRequest().permitAll())
                .addFilterBefore(jwtAuthorizationFilter, LogoutFilter.class)
                .oauth2Login(login -> login
                        .authorizationEndpoint(config -> config
                                .baseUri(oAuth2EndpointsProperties.authorize)
                                .authorizationRequestRepository(oAuth2AuthorizationRequestRepository)
                        )
                        .redirectionEndpoint(config -> config.baseUri(
                                oAuth2EndpointsProperties.callback + OAuth2Constant.OAUTH2_PROVIDER_SUFFIX
                        ))
                        .userInfoEndpoint(config -> config.userService(oAuth2Service))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(servletExceptionHandler::entryPoint)
                        .accessDeniedHandler(servletExceptionHandler::accessDenied)
                )
                .logout(config -> config
                        .logoutUrl("/api/logout")
                        .addLogoutHandler(logoutRequestHandler)
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                )
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public ServletExceptionHandler servletExceptionHandler(ObjectMapper objectMapper) {
        return new ServletExceptionHandler(objectMapper);
    }

    @Bean
    public JwtConfiguration.JwtProperties jwtProperties() {
        return new JwtConfiguration.JwtProperties();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(
            OAuth2EndpointsProperties oAuth2EndpointsProperties,
            JwtConfiguration.JwtProperties jwtProperties
    ) {
        final var permitPaths = List.of(
                oAuth2EndpointsProperties.authorize + OAuth2Constant.OAUTH2_PROVIDER_SUFFIX,
                oAuth2EndpointsProperties.callback + OAuth2Constant.OAUTH2_PROVIDER_SUFFIX
        );
        return new JwtAuthorizationFilter(permitPaths, jwtProperties);
    }

    @Bean
    public OAuth2AuthorizationRequestRepository oAuth2AuthorizeRequestResolver(
            StringRedisTemplate stringRedisTemplate
    ) {
        return new OAuth2AuthorizationRequestRepository(stringRedisTemplate);
    }

    @Bean
    public JwtProvider jwtProvider(JwtConfiguration.JwtProperties jwtProperties) {
        return new JwtProvider(jwtProperties);
    }

    @Bean
    public OAuth2LoginSuccessHandler oAuth2SuccessProcessHandler(
            JwtProvider jwtProvider,
            @Value("${app.security.oauth2.route.front}") String frontRoute,
            @Value("${app.security.oauth2.endpoints.redirect}") String redirectEndpoint,
            RefreshTokenDao refreshTokenRepositoryHandler,
            RouteRedirectStrategy routeRedirectStrategy
    ) {
        var redirectPath = (frontRoute.equals("/") ? null : frontRoute) + redirectEndpoint;
        final var handler = new OAuth2LoginSuccessHandler(jwtProvider, redirectPath, refreshTokenRepositoryHandler);
        handler.setRedirectStrategy(routeRedirectStrategy);
        return handler;
    }

    @Bean
    public OAuth2LoginFailureHandler oAuth2FailureProcessHandler(
            RouteRedirectStrategy routeRedirectStrategy,
            @Value("${app.security.oauth2.route.front}") String frontRoute,
            @Value("${app.security.oauth2.endpoints.redirect}") String redirectEndpoint
    ) {
        var redirectPath = (frontRoute.equals("/") ? null : frontRoute) + redirectEndpoint;
        final var handler = new OAuth2LoginFailureHandler(redirectPath);
        handler.setRedirectStrategy(routeRedirectStrategy);
        return handler;
    }

    @Bean
    public LogoutRequestHandler logoutRequestHandler(
            LogoutService logoutService, JwtProvider jwtProvider) {
        return new LogoutRequestHandler(logoutService, jwtProvider);
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler(
            @Value("${network.route.gateway}") String gatewayRoute) {
        return new LogoutSuccessHandler(gatewayRoute);
    }

    @Getter
    @Setter
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "app.security.oauth2.endpoints")
    public static class OAuth2EndpointsProperties {
        private String authorize;
        private String callback;
        private String error;
        private String redirect;
    }

    @Getter
    @Setter
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "app.security.roles")
    public static class RoleProperties {
        private String primary;
    }
}
