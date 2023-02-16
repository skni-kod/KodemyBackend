package pl.sknikod.kodemy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import pl.sknikod.kodemy.auth.AuthCookieAuthorizationRequestRepository;
import pl.sknikod.kodemy.auth.AuthEntryPoint;
import pl.sknikod.kodemy.auth.handler.AuthAuthorizationFailureHandler;
import pl.sknikod.kodemy.auth.handler.AuthAuthorizationSuccessHandler;
import pl.sknikod.kodemy.auth.handler.AuthLogoutSuccessHandler;
import pl.sknikod.kodemy.auth.oauth2.OAuth2UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private AuthCookieAuthorizationRequestRepository authCookieAuthorizationRequestRepository;
    @Autowired
    private AuthEntryPoint authEntryPoint;
    @Autowired
    private OAuth2UserService authUserService;
    @Autowired
    private AuthAuthorizationSuccessHandler authAuthorizationSuccessHandler;
    @Autowired
    private AuthAuthorizationFailureHandler authAuthorizationFailureHandler;
    @Autowired
    private AuthLogoutSuccessHandler authLogoutSuccessHandler;
    public final static String AUTH_REQUEST_BASE_URI = "/api/oauth2/authorize";
    @Value("${springdoc.api-docs.path}")
    private String apiDocsPath;
    @Value("${springdoc.swagger-ui.path}")
    private String swaggerUiPath;

    private String[] RequestGetWhitelist() {
        return new String[]{
                //Base
                "/",
                "/api",
                "/error",
                //Auth
                "/api/oauth2/**",
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
                .cors()
                .and()
                .csrf()
                .disable();
        http
                .authorizeRequests(autz -> autz
                        .antMatchers(HttpMethod.GET, RequestGetWhitelist()).permitAll()
                        .anyRequest().authenticated()
                );
        http
                .formLogin()
                .disable()
                //.httpBasic()
                //.disable()
                .exceptionHandling()
                .authenticationEntryPoint(authEntryPoint)
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri(AUTH_REQUEST_BASE_URI)
                .authorizationRequestRepository(authCookieAuthorizationRequestRepository)
                .and()
                .redirectionEndpoint()
                .baseUri("/api/oauth2/callback/**")
                .and()
                .userInfoEndpoint()
                .userService(authUserService)
                .and()
                .successHandler(authAuthorizationSuccessHandler)
                .failureHandler(authAuthorizationFailureHandler)
                .and()
                .logout()
                .logoutUrl("api/logout")
                .logoutSuccessHandler(authLogoutSuccessHandler)
                .invalidateHttpSession(true)
                .deleteCookies(
                        "JSESSIONID",
                        AuthCookieAuthorizationRequestRepository.AUTHORIZATION_REQUEST_COOKIE_NAME
                );
        return http.build();
    }
}
