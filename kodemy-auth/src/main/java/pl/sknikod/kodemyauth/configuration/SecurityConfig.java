package pl.sknikod.kodemyauth.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Option;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pl.sknikod.kodemyauth.exception.ExceptionRestGenericMessage;
import pl.sknikod.kodemyauth.infrastructure.auth.AuthService;
import pl.sknikod.kodemyauth.infrastructure.auth.AuthorizationRequestRepositoryImpl;
import pl.sknikod.kodemyauth.infrastructure.auth.handler.AuthAuthenticationFailureHandler;
import pl.sknikod.kodemyauth.infrastructure.auth.handler.AuthAuthenticationSuccessHandler;
import pl.sknikod.kodemyauth.infrastructure.auth.handler.AuthLogoutSuccessHandler;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Role;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@AllArgsConstructor
@DependsOn(value = {
        "securityConfig.SecurityProperties.AuthProperties",
        "securityConfig.SecurityProperties.CorsProperties",
})
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityProperties.AuthProperties authProperties,
            AuthorizationRequestRepositoryImpl authorizationRequestRepository,
            AuthService authService,
            AuthAuthenticationSuccessHandler authAuthenticationSuccessHandler,
            AuthAuthenticationFailureHandler authAuthenticationFailureHandler,
            ObjectMapper objectMapper,
            AuthLogoutSuccessHandler authLogoutSuccessHandler
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable).cors()
                .and()
                .authorizeHttpRequests(autz -> autz.anyRequest().permitAll())
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(login -> login
                        .authorizationEndpoint()
                        .baseUri(authProperties.uri.login)
                        .authorizationRequestRepository(authorizationRequestRepository)
                        .and()
                        .redirectionEndpoint()
                        .baseUri(authProperties.uri.callback)
                        .and()
                        .userInfoEndpoint()
                        .userService(authService)
                        .and()
                        .successHandler(authAuthenticationSuccessHandler)
                        .failureHandler(authAuthenticationFailureHandler)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((req, res, e) ->
                                writeBodyResponseForHandler(res, objectMapper, e, HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((req, res, e) ->
                                writeBodyResponseForHandler(res, objectMapper, e, HttpStatus.FORBIDDEN))
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher(authProperties.uri.logout, HttpMethod.GET.name())
                        )
                        .logoutSuccessHandler(authLogoutSuccessHandler)
                        .invalidateHttpSession(false)
                        .deleteCookies(
                                "JSESSIONID",
                                authProperties.key.currentSession,
                                authProperties.key.jwt
                        )
                );
        return http.build();
    }

    private void writeBodyResponseForHandler(
            HttpServletResponse response,
            ObjectMapper objectMapper,
            Exception ex,
            HttpStatus status
    ) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.getWriter().write(objectMapper.writeValueAsString(new ExceptionRestGenericMessage(status, ex)));
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SecurityProperties {

        @Configuration
        @Data
        @ConfigurationProperties(prefix = "app.security.auth")
        public static class AuthProperties {
            private UriProperties uri;
            private KeyProperties key;

            @Getter
            @Setter
            public static class UriProperties {
                private String login;
                private String callback;
                private String logout;
            }

            @Getter
            @Setter
            public static class KeyProperties {
                private String currentSession;
                private String redirect;
                private String jwt;
            }
        }

        @Configuration
        @Data
        @ConfigurationProperties(prefix = "app.security.role")
        public static class RoleProperties {
            private String defaultRole;
            private Map<String, Set<String>> privileges = new LinkedHashMap<>();

            public Set<SimpleGrantedAuthority> getPrivileges(Role.RoleName role) {
                return Option
                        .of(privileges.get(role.toString()))
                        .map(Collection::stream)
                        .map(stream -> stream.map(SimpleGrantedAuthority::new).collect(Collectors.toSet()))
                        .getOrElse(Collections::emptySet);
            }
        }

        @Configuration
        @Data
        @ConfigurationProperties(prefix = "app.security.cors")
        public static class CorsProperties {
            private String[] allowedUris;
        }
    }
}