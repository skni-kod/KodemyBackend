package pl.sknikod.kodemy.infrastructure.technology.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import pl.sknikod.kodemy.infrastructure.technology.TechnologyService;

import java.net.URI;

@Controller
@AllArgsConstructor
public class TechnologyController implements TechnologyControllerDefinition {
    private TechnologyService technologyService;

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TechnologyAddResponse> addTechnology(TechnologyAddRequest tech) {
        var technologyResponse = technologyService.addTechnology(tech);
        return ResponseEntity
                .created(URI.create("/api/technologies/" + technologyResponse.getId()))
                .body(technologyResponse);
    }
}
