package pl.sknikod.kodemyauth.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.OAuth2ProviderService;
import pl.sknikod.kodemycommons.doc.SwaggerResponse;

import java.util.List;

@Tag(name = "OAuth2")
@SwaggerResponse
@SwaggerResponse.SuccessCode200
@RequestMapping("/api/oauth2")
public interface OAuth2ControllerDefinition {
    @GetMapping("/providers")
    @Operation(summary = "Show all OAuth2 providers")
    ResponseEntity<List<OAuth2ProviderService.ProviderResponse>> getProvidersList();
}