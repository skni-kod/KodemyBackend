package pl.sknikod.kodemyauth.infrastructure.auth.rest;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Provider;
import pl.sknikod.kodemyauth.util.SwaggerResponse;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/api/oauth2")
@Tag(name = "OAuth2")
@SwaggerResponse
@SwaggerResponse.SuccessCode200
public interface AuthControllerDefinition {
    String REDIRECT_URI_PARAMETER = "redirect_uri";

    @GetMapping(value = "/links")
    @Operation(summary = "Get links for OAUTH2")
    ResponseEntity<OAuth2LinksResponse> getLinks(
            @RequestParam(name = REDIRECT_URI_PARAMETER, required = false) String redirectUri,
            HttpServletRequest request
    );

    @GetMapping("/providers")
    @Operation(summary = "Show all OAuth2 providers")
    ResponseEntity<Provider.ProviderType[]> getProvidersList();

    @GetMapping
    @Operation(summary = "Check if user is authenticated")
    ResponseEntity<AuthInfo> isAuthenticated();

    @GetMapping("/token")
    @Operation(summary = "Get authentication token")
    ResponseEntity<AuthResponse> getToken();

    @Hidden
    @GetMapping("/validateToken")
    ResponseEntity<TokenInfo> validateToken(@RequestParam String bearer);
}