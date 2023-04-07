package pl.sknikod.kodemy.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.sknikod.kodemy.rest.response.UserOAuth2MeResponse;
import pl.sknikod.kodemy.user.UserProviderType;
import pl.sknikod.kodemy.util.SwaggerResponse;

@RequestMapping("/api/oauth2")
@Tag(name = "OAuth2")
@SwaggerResponse
@SwaggerResponse.SuccessCode
public interface AuthControllerDefinition {
    @GetMapping("/authorize/{provider}")
    @Operation(summary = "Sign in via OAuth2 (ONLY WORK OUTSIDE SWAGGER)")
    void authorize(@PathVariable UserProviderType provider, @RequestParam String redirect_uri);

    @GetMapping("/logout")
    @Operation(summary = "Logout (ONLY WORK OUTSIDE SWAGGER)")
    void logout(@RequestParam String redirect_uri);

    @GetMapping("/providers")
    @Operation(summary = "Get all OAuth2 providers")
    ResponseEntity<UserProviderType[]> getProvidersList();

    @GetMapping("/me")
    @Operation(summary = "Get information about logged user")
    @SwaggerResponse.AuthRequest
    ResponseEntity<UserOAuth2MeResponse> getUserInfo();
}