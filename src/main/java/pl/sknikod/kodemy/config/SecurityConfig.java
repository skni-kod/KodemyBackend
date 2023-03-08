package pl.sknikod.kodemy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository;
import pl.sknikod.kodemy.auth.AuthEntryPoint;
import pl.sknikod.kodemy.auth.AuthService;
import pl.sknikod.kodemy.auth.handler.*;
import pl.sknikod.kodemy.util.ExceptionRestHandler;

import static pl.sknikod.kodemy.auth.AuthController.DEFAULT_REDIRECT_URL_AFTER_LOGOUT;
import static pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository.AUTHORIZATION_REQUEST_COOKIE_NAME;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig {
    @Autowired
    private AuthCookieAuthorizationRequestRepository authCookieAuthorizationRequestRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private AuthAuthenticationSuccessHandler authAuthenticationSuccessHandler;
    @Autowired
    private AuthAuthenticationFailureHandler authAuthenticationFailureHandler;
    @Autowired
    private AuthEntryPoint authEntryPoint;
    @Autowired
    private AuthAccessDeniedHandler authAccessDeniedHandler;
    @Autowired
    private AuthLogoutHandler authLogoutHandler;
    @Autowired
    private AuthLogoutSuccessHandler authLogoutSuccessHandler;
    public final static String AUTH_REQUEST_BASE_URI = "/api/oauth2/authorize";
    @Value("${springdoc.api-docs.path}")
    private String apiDocsPath;
    @Value("${springdoc.swagger-ui.path}")
    private String swaggerUiPath;
    @Autowired
    private ExceptionRestHandler exceptionRestHandler;

    private String[] RequestGetWhitelist() {
        return new String[]{
                //Base
                "/",
                "/**/favicon.ico",
                "/api",
                "/error",
                //Auth
                "/api/oauth2/**",
                "/api/logout",
                //OpenAPI
                "/api/swagger-ui/**",
                "/api/docs/swagger-config",
                apiDocsPath,
                swaggerUiPath,
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().disable()
                .authorizeRequests(autz -> autz
                        .antMatchers(HttpMethod.GET, RequestGetWhitelist()).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin().disable()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri(AUTH_REQUEST_BASE_URI)
                .authorizationRequestRepository(authCookieAuthorizationRequestRepository)
                .and()
                .redirectionEndpoint()
                .baseUri("/api/oauth2/callback/**")
                .and()
                .userInfoEndpoint()
                .userService(authService)
                .and()
                .successHandler(authAuthenticationSuccessHandler)
                .failureHandler(authAuthenticationFailureHandler)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(authAccessDeniedHandler)
                .and()
                .logout()
                .logoutRequestMatcher(
                        new AntPathRequestMatcher("/api/oauth2/logout", HttpMethod.GET.name())
                )
                .addLogoutHandler(authLogoutHandler)
                .logoutSuccessHandler(authLogoutSuccessHandler)
                .logoutSuccessUrl(DEFAULT_REDIRECT_URL_AFTER_LOGOUT)
                .invalidateHttpSession(true)
                .deleteCookies(
                        "JSESSIONID",
                        AUTHORIZATION_REQUEST_COOKIE_NAME
                );
        return http.build();
    }
}