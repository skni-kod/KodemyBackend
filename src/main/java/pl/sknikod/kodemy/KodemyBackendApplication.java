package pl.sknikod.kodemy;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "KodemyBackend API", version = "PRE-ALPHA", description = "SKNI Kod Kodemy"
))
public class KodemyBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(KodemyBackendApplication.class, args);
    }
}