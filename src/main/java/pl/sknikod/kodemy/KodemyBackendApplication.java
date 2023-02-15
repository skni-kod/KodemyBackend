package pl.sknikod.kodemy;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "KodemyBackend API", version = "1.0", description = "SKNI KOD Kodemy"
))
public class KodemyBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(KodemyBackendApplication.class, args);
    }
}