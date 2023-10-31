package pl.sknikod.kodemybackend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.sknikod.kodemybackend.exception.ExceptionRestGenericMessage;
import pl.sknikod.kodemybackend.util.filter.JwtAuthorizationFilter;

import java.util.Set;

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
    private final ObjectMapper objectMapper;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final SecurityConfig.SecurityProperties securityProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(autz -> autz.anyRequest().permitAll())
                .formLogin().disable()
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((req, res, e) ->
                                ExceptionRestGenericMessage.writeBodyResponseForHandler(res, objectMapper, e, HttpStatus.UNAUTHORIZED)
                        )
                        .accessDeniedHandler((req, res, e) ->
                                ExceptionRestGenericMessage.writeBodyResponseForHandler(res, objectMapper, e, HttpStatus.FORBIDDEN)
                        )
                )
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
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

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Value
    public static class JwtUserDetails implements UserDetails {
        Long id;
        String username;
        String password = null;
        Set<? extends GrantedAuthority> authorities;
        boolean isAccountNonExpired = true;
        boolean isAccountNonLocked = true;
        boolean isCredentialsNonExpired = true;
        boolean isEnabled = true;
    }

    @Configuration
    @Data
    @ConfigurationProperties(prefix = "kodemy.security")
    public static class SecurityProperties {
        private CorsProperties cors;

        @Getter
        @Setter
        public static class CorsProperties {
            private String[] allowedUris;
        }
    }
}