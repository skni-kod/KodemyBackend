package pl.sknikod.kodemyauth.infrastructure.auth.rest;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @GetMapping(value = "/link-for-authorize-{provider}", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Get URL for AUTHORIZE via OAUTH2")
    ResponseEntity<String> authorize(
            @PathVariable Provider.ProviderType provider,
            @Parameter(description = "Leave empty to redirect here")
            @RequestParam(name = "redirect_uri", required = false, defaultValue = "https://") String redirectUri,
            HttpServletRequest request
    );

    @GetMapping(value = "/link-for-logout", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Get URL for LOGOUT for OAUTH2")
    ResponseEntity<String> logout(
            @Parameter(description = "Leave empty to redirect here")
            @RequestParam(name = "redirect_uri", required = false, defaultValue = "https://") String redirectUri,
            HttpServletRequest request
    );

    @GetMapping("/providers")
    @Operation(summary = "Show all OAuth2 providers")
    ResponseEntity<Provider.ProviderType[]> getProvidersList();

    @GetMapping
    @Operation(summary = "Check if user is authenticated")
    ResponseEntity<AuthInfo> isAuthenticated();


    @Hidden
    @GetMapping("/token")
    ResponseEntity<AuthResponse> getToken();

    @Hidden
    @GetMapping("/validate")
    ResponseEntity<TokenInfo> validateToken(@RequestParam String bearer);
}