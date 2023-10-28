package pl.sknikod.kodemysearch;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
		title = "KodemyBackend API - kodemy-search", version = "", description = "SKNI Kod Kodemy"
))
public class KodemySearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(KodemySearchApplication.class, args);
	}

}
