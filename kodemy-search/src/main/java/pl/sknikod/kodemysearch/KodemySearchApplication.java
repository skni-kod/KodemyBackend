package pl.sknikod.kodemysearch;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "KodemyBackend API - kodemy-search",
        version = "",
        description = "SKNI Kod Kodemy"
), security = @SecurityRequirement(name = "Bearer Authentication"))
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class KodemySearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(KodemySearchApplication.class, args);
    }

}
