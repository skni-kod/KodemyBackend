package pl.sknikod.kodemyauth.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Option;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.sknikod.kodemyauth.exception.ExceptionRestGenericMessage;
import pl.sknikod.kodemyauth.infrastructure.auth.AuthService;
import pl.sknikod.kodemyauth.infrastructure.auth.AuthorizationRequestRepositoryImpl;
import pl.sknikod.kodemyauth.infrastructure.auth.handler.AuthAuthenticationFailureHandler;
import pl.sknikod.kodemyauth.infrastructure.auth.handler.AuthAuthenticationSuccessHandler;
import pl.sknikod.kodemyauth.infrastructure.auth.handler.AuthLogoutHandler;
import pl.sknikod.kodemyauth.infrastructure.auth.handler.AuthLogoutSuccessHandler;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Role;

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
@DependsOn("securityConfig.SecurityProperties")
public class SecurityConfig {
    private final AuthorizationRequestRepositoryImpl authorizationRequestRepository;
    private final AuthService authService;
    private final AuthAuthenticationSuccessHandler authAuthenticationSuccessHandler;
    private final AuthAuthenticationFailureHandler authAuthenticationFailureHandler;
    private final AuthLogoutHandler authLogoutHandler;
    private final AuthLogoutSuccessHandler authLogoutSuccessHandler;
    private final SecurityProperties securityProperties;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(autz -> autz
                        .anyRequest().permitAll()
                )
                .formLogin().disable()
                .oauth2Login(login -> login
                        .authorizationEndpoint()
                        .baseUri(securityProperties.getAuth().getLoginUri())
                        .authorizationRequestRepository(authorizationRequestRepository)
                        .and()
                        .redirectionEndpoint()
                        .baseUri(securityProperties.getAuth().getCallbackUri())
                        .and()
                        .userInfoEndpoint()
                        .userService(authService)
                        .and()
                        .successHandler(authAuthenticationSuccessHandler)
                        .failureHandler(authAuthenticationFailureHandler)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((req, res, e) ->
                                ExceptionRestGenericMessage.writeBodyResponseForHandler(res, objectMapper, e, HttpStatus.UNAUTHORIZED)
                        )
                        .accessDeniedHandler((req, res, e) ->
                                ExceptionRestGenericMessage.writeBodyResponseForHandler(res, objectMapper, e, HttpStatus.FORBIDDEN)
                        )
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(
                                new AntPathRequestMatcher(securityProperties.getAuth().getLogoutUri(), HttpMethod.GET.name())
                        )
                        .addLogoutHandler(authLogoutHandler)
                        .logoutSuccessHandler(authLogoutSuccessHandler)
                        .invalidateHttpSession(false)
                        .deleteCookies(
                                "JSESSIONID"
                        )
                )
                .cors();
        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .allowedOrigins(securityProperties.getCors().getAllowedUris())
                        .allowCredentials(true);
            }
        };
    }

    @Configuration
    @Data
    @ConfigurationProperties(prefix = "kodemy.security")
    public static class SecurityProperties {
        private AuthProperties auth;
        private RoleProperties role;
        private CorsProperties cors;

        @Getter
        @Setter
        public static class AuthProperties {
            private String loginUri;
            private String callbackUri;
            private String logoutUri;
        }

        @Getter
        @Setter
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

        @Getter
        @Setter
        public static class CorsProperties {
            private String[] allowedUris;
        }
    }
}