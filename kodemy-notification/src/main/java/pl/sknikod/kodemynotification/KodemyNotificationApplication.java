package pl.sknikod.kodemynotification;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@EnableDiscoveryClient
public class KodemyNotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(KodemyNotificationApplication.class, args);
    }

}
