package pl.sknikod.kodemybackend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "KodemyBackend API - kodemy-backend",
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
})
public class KodemyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(KodemyBackendApplication.class, args);
    }

}
