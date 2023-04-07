package pl.sknikod.kodemy.config.actuate;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.util.SwaggerResponse;

@RestController
@Tag(name = "Actuator")
@AllArgsConstructor
@SwaggerResponse
@SwaggerResponse.AuthRequest
public class ActuatorController {
    private ConfigurableApplicationContext context;

    @PostMapping("api/actuator/refresh")
    @SwaggerResponse.BadRequestCode
    @PreAuthorize("isAuthenticated() and hasAuthority('CAN_REFRESH_CONFIG')")
    public void refresh() {
        context.refresh();
    }
}