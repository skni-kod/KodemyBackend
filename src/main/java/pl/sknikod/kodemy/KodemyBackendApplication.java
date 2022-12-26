package pl.sknikod.kodemy;

import pl.sknikod.kodemy.type.Type;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import pl.sknikod.kodemy.type.TypeRepository;

@SpringBootApplication
@AllArgsConstructor
public class KodemyBackendApplication {

	private final TypeRepository typeRepository;
	public static void main(String[] args) {
		SpringApplication.run(KodemyBackendApplication.class, args);
	}
}
