package pl.sknikod.kodemysearch.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import pl.sknikod.kodemysearch.exception.ExceptionRestGenericMessage;
import pl.sknikod.kodemysearch.util.filter.JwtAuthorizationFilter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ObjectMapper objectMapper,
            JwtAuthorizationFilter jwtAuthorizationFilter
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable).cors()
                .and()
                .authorizeHttpRequests(autz -> autz.anyRequest().permitAll())
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((req, res, e) ->
                                writeBodyResponseForHandler(res, objectMapper, e, HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((req, res, e) ->
                                writeBodyResponseForHandler(res, objectMapper, e, HttpStatus.FORBIDDEN))
                )
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
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
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Value
    public static class UserPrincipal implements UserDetails {
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
    @ConfigurationProperties(prefix = "app.security")
    public static class SecurityProperties {
        private CorsProperties cors;

        @Getter
        @Setter
        public static class CorsProperties {
            private String[] allowedUris;
        }
    }
}