package pl.sknikod.kodemy.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pl.sknikod.kodemy.auth.AuthService;
import pl.sknikod.kodemy.auth.AuthenticationEntryPointImpl;
import pl.sknikod.kodemy.auth.AuthorizationRequestRepositoryImpl;
import pl.sknikod.kodemy.auth.handler.*;
import pl.sknikod.kodemy.util.filter.RefreshUserPrincipalFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@AllArgsConstructor
public class SecurityConfig {
    private AuthorizationRequestRepositoryImpl authorizationRequestRepository;
    private AuthService authService;
    private AuthAuthenticationSuccessHandler authAuthenticationSuccessHandler;
    private AuthAuthenticationFailureHandler authAuthenticationFailureHandler;
    private AuthenticationEntryPointImpl authEntryPoint;
    private AuthAccessDeniedHandler authAccessDeniedHandler;
    private AuthLogoutHandler authLogoutHandler;
    private AuthLogoutSuccessHandler authLogoutSuccessHandler;
    private AppConfig.SecurityAuthProperties authProperties;
    private RefreshUserPrincipalFilter refreshUserPrincipalFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .addFilterBefore(refreshUserPrincipalFilter, FilterSecurityInterceptor.class)
                .authorizeHttpRequests(autz -> autz
                        .anyRequest().permitAll()
                )
                .formLogin().disable()
                .oauth2Login(login -> login
                        .authorizationEndpoint()
                        .baseUri(authProperties.getLoginUri())
                        .authorizationRequestRepository(authorizationRequestRepository)
                        .and()
                        .redirectionEndpoint()
                        .baseUri(authProperties.getCallbackUri())
                        .and()
                        .userInfoEndpoint()
                        .userService(authService)
                        .and()
                        .successHandler(authAuthenticationSuccessHandler)
                        .failureHandler(authAuthenticationFailureHandler)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(authAccessDeniedHandler)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(
                                new AntPathRequestMatcher(authProperties.getLogoutUri(), HttpMethod.GET.name())
                        )
                        .addLogoutHandler(authLogoutHandler)
                        .logoutSuccessHandler(authLogoutSuccessHandler)
                        .invalidateHttpSession(false)
                        .deleteCookies(
                                "JSESSIONID"
                        )
                );
        return http.build();
    }
}