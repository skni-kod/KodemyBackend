package pl.sknikod.kodemy.infrastructure.health.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("${management.endpoints.web.base-path}")
@PreAuthorize("isAuthenticated() and hasAuthority('CAN_USE_ACTUATOR')")
@Tag(name = "Actuator")
public class ActuatorController {
    private final ConfigurableApplicationContext context;
    private final List<InfoContributor> infoContributors;

    @GetMapping("/info")
    public Map<String, Object> info() {
        final var builder = new Info.Builder();
        infoContributors.forEach(c -> c.contribute(builder));
        return builder.build().getDetails();
    }

    @PostMapping("/refresh")
    public void refresh() {
        context.refresh();
    }
}
