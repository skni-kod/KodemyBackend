package pl.sknikod.kodemy.infrastructure.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import pl.sknikod.kodemy.infrastructure.rest.model.TechnologyAddRequest;
import pl.sknikod.kodemy.infrastructure.rest.model.TechnologyAddResponse;

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
