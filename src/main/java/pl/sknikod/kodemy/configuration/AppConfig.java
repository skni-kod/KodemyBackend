package pl.sknikod.kodemy.configuration;

import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.sknikod.kodemy.infrastructure.common.entity.RoleName;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@Import({
        SecurityConfig.class,
        RabbitMQConfig.class
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppConfig {

    @Configuration
    @Data
    @ConfigurationProperties(prefix = "kodemy.security.auth")
    public static class SecurityAuthProperties {
        private String loginUri;
        private String callbackUri;
        private String logoutUri;
    }

    @Configuration
    @Data
    @RefreshScope
    @ConfigurationProperties(prefix = "kodemy.security.role")
    public static class SecurityRoleProperties {
        private String defaultRole;
        private Map<String, Set<String>> privileges = new LinkedHashMap<>();

        public Set<SimpleGrantedAuthority> getPrivileges(RoleName role) {
            return Option
                    .of(privileges.get(role.toString()))
                    .map(Collection::stream)
                    .map(stream -> stream.map(SimpleGrantedAuthority::new).collect(Collectors.toSet()))
                    .getOrElse(Collections::emptySet);
        }
    }
}
