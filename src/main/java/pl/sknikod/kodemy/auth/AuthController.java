package pl.sknikod.kodemy.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemy.user.UserProviderType;

@RestController
@RequestMapping("/api/oauth2")
@Tag(name = "OAuth2")
public class AuthController {
    public final static String DEFAULT_REDIRECT_URL_AFTER_LOGIN = "http://localhost:8181/api/me";

    @GetMapping("/authorize/{provider}")
    @Operation(summary = "Sign in via OAuth2")
    public void authorize(@PathVariable UserProviderType provider, @RequestParam String redirect_uri) {}

    @GetMapping("/providers")
    @Operation(summary = "Get all OAuth2 providers")
    public ResponseEntity<?> getProvidersList(){
        return ResponseEntity.ok(UserProviderType.values());
    }
}