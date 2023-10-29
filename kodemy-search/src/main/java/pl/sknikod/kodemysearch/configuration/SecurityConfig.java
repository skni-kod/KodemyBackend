package pl.sknikod.kodemysearch.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import pl.sknikod.kodemysearch.exception.ExceptionRestGenericMessage;
import pl.sknikod.kodemysearch.util.filter.JwtAuthorizationFilter;

import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@AllArgsConstructor
public class SecurityConfig {
    private final ObjectMapper objectMapper;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

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
                );
        return http.build();
    }

    @Bean
    public RestTemplate restTemplate(){
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
}