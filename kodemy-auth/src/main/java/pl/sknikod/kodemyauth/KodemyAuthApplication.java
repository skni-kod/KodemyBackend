package pl.sknikod.kodemyauth;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "KodemyBackend API - kodemy-auth",
                version = "",
                description = "SKNI Kod Kodemy"
        ),
        security = {
                @SecurityRequirement(name = "authorization_bearer")
        }
)
@SecuritySchemes({
        @SecurityScheme(
                name = "authorization_bearer",
                type = SecuritySchemeType.HTTP,
                scheme = "bearer",
                bearerFormat = "JWT"
        ),
        @SecurityScheme(
                name = "oauth2",
                type = SecuritySchemeType.OAUTH2,
                description = """
                        OAuth2 authorization is handled by the running kodemy-api-gateway service.\n
                        No client_id or client_secret required.""",
                flows = @OAuthFlows(authorizationCode = @OAuthFlow(
                        authorizationUrl = "${service.baseUrl.gateway}${app.security.oauth2.endpoint.authorize}/github"
                ))
        )
})
@EnableDiscoveryClient
public class KodemyAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(KodemyAuthApplication.class, args);
    }

}
