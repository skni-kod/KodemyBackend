package pl.sknikod.kodemybackend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
		title = "KodemyBackend API - kodemy-backend", version = "", description = "SKNI Kod Kodemy"
))
public class KodemyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(KodemyBackendApplication.class, args);
	}

}
