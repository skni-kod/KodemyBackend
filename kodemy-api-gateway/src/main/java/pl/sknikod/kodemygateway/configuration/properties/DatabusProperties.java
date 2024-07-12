package pl.sknikod.kodemygateway.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@Data
@ConfigurationProperties(prefix = "internal.databus")
public class DatabusProperties {
    private static final Base64.Encoder encoder = Base64.getEncoder();
    public static final String AUTHORIZATION_PREFIX = "Basic ";

    private String username;
    private String password;

    public String getBasicAuthHeader() {
        String auth = username + ":" + password;
        return AUTHORIZATION_PREFIX + encoder.encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }
}