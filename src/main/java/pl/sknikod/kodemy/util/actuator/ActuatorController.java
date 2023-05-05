package pl.sknikod.kodemy.util.actuator;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${management.endpoints.web.base-path}")
@PreAuthorize("isAuthenticated() and hasAuthority('CAN_USE_ACTUATOR')")
@Tag(name = "Actuator")
public class ActuatorController {
    private final ConfigurableApplicationContext context;

    @PostMapping("/refresh")
    public void refresh() {
        context.refresh();
    }
}
