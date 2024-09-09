package pl.sknikod.kodemyauth.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.infrastructure.database.handler.RefreshTokenRepositoryHandler;
import pl.sknikod.kodemyauth.infrastructure.module.auth.handler.LogoutSuccessHandler;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.OAuth2AuthorizeRequestResolver;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.OAuth2Service;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.OAuth2SessionAuthRequestRepository;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.handler.OAuth2LoginFailureHandler;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.handler.OAuth2LoginSuccessHandler;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2Constant;
import pl.sknikod.kodemyauth.util.data.AuditorAwareAdapter;
import pl.sknikod.kodemyauth.util.route.RouteRedirectStrategy;
import pl.sknikod.kodemycommon.exception.handler.ServletExceptionHandler;
import pl.sknikod.kodemycommon.security.JwtAuthorizationFilter;
import pl.sknikod.kodemycommon.security.JwtProvider;
import pl.sknikod.kodemycommon.security.configuration.JwtConfiguration;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true)
@RequiredArgsConstructor
@Import({JwtConfiguration.class})
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthorizationFilter jwtAuthorizationFilter,
            OAuth2AuthorizeRequestResolver oAuth2AuthorizeRequestResolver,
            OAuth2SessionAuthRequestRepository oAuth2SessionAuthRequestRepository,
            OAuth2PathProperties oAuth2PathProperties,
            OAuth2Service oAuth2Service,
            OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
            OAuth2LoginFailureHandler oAuth2LoginFailureHandler,
            ServletExceptionHandler servletExceptionHandler,
            LogoutSuccessHandler logoutSuccessHandler
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(autz -> autz.anyRequest().permitAll())
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(login -> login
                        .authorizationEndpoint(config -> config
                                .authorizationRequestResolver(oAuth2AuthorizeRequestResolver)
                                .authorizationRequestRepository(oAuth2SessionAuthRequestRepository)
                        )
                        .redirectionEndpoint(config -> config.baseUri((
                                oAuth2PathProperties.callback + OAuth2Constant.OAUTH2_PROVIDER_SUFFIX
                        )))
                        .userInfoEndpoint(config -> config.userService(oAuth2Service))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(servletExceptionHandler::entryPoint)
                        .accessDeniedHandler(servletExceptionHandler::accessDenied)
                )
                .logout(config -> config.logoutSuccessHandler(logoutSuccessHandler))
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public AuditorAware<?> auditorAware() {
        return new AuditorAwareAdapter();
    }

    @Bean
    public ServletExceptionHandler servletExceptionHandler(ObjectMapper objectMapper){
        return new ServletExceptionHandler(objectMapper);
    }

    @Bean
    public JwtConfiguration.JwtProperties jwtProperties(){
        return new JwtConfiguration.JwtProperties();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(
            SecurityConfig.OAuth2PathProperties oAuth2PathProperties,
            JwtConfiguration.JwtProperties jwtProperties
    ){
        final var permitPaths = List.of(oAuth2PathProperties.getCallback() + OAuth2Constant.OAUTH2_PROVIDER_SUFFIX);
        return new JwtAuthorizationFilter(permitPaths, jwtProperties);
    }

    @Bean
    public OAuth2AuthorizeRequestResolver oAuth2AuthorizeRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository,
            SecurityConfig.OAuth2PathProperties oAuth2PathProperties
    ) {
        return new OAuth2AuthorizeRequestResolver(
                clientRegistrationRepository, oAuth2PathProperties.getAuthorize());
    }

    @Bean
    public OAuth2SessionAuthRequestRepository oAuth2SessionAuthRequestRepository(
            ClientRegistrationRepository clientRegistrationRepository,
            SecurityConfig.OAuth2PathProperties oAuth2PathProperties
    ) {
        return new OAuth2SessionAuthRequestRepository(clientRegistrationRepository,
                oAuth2PathProperties.getCallback() + OAuth2Constant.OAUTH2_PROVIDER_SUFFIX);
    }

    @Bean
    public JwtProvider jwtProvider(JwtConfiguration.JwtProperties jwtProperties){
        return new JwtProvider(jwtProperties);
    }

    @Bean
    public OAuth2LoginSuccessHandler oAuth2SuccessProcessHandler(
            RouteRedirectStrategy routeRedirectStrategy,
            JwtProvider jwtProvider,
            @Value("${network.routes.front}") String frontRouteBaseUrl,
            RefreshTokenRepositoryHandler refreshTokenRepositoryHandler
    ) {
        final var handler = new OAuth2LoginSuccessHandler(jwtProvider, frontRouteBaseUrl, refreshTokenRepositoryHandler);
        handler.setRedirectStrategy(routeRedirectStrategy);
        return handler;
    }

    @Bean
    public OAuth2LoginFailureHandler oAuth2FailureProcessHandler(
            RouteRedirectStrategy routeRedirectStrategy,
            @Value("${network.routes.front}") String frontRouteBaseUrl
    ) {
        final var handler = new OAuth2LoginFailureHandler(frontRouteBaseUrl);
        handler.setRedirectStrategy(routeRedirectStrategy);
        return handler;
    }

    @Getter
    @Setter
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "spring.security.oauth2.path")
    public static class OAuth2PathProperties {
        private String authorize;
        private String callback;
        private String error;
    }

    @Getter
    @Setter
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "app.security.roles")
    public static class RoleProperties {
        private String primary;
        private LinkedHashMap<String, Set<SimpleGrantedAuthority>> authorities = new LinkedHashMap<>();

        public Set<SimpleGrantedAuthority> getAuthorities(String role) {
            return authorities.getOrDefault(role, Collections.emptySet());
        }
    }
}
