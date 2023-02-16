package pl.sknikod.kodemy.auth;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemy.user.UserProviderType;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/oauth2")
@Tag(name = "OAuth2")
public class AuthController {
    @Hidden
    @GetMapping("/authorize/{provider}")
    @Operation(summary = "Sign in via OAuth2")
    public void authorize(@PathVariable UserProviderType provider, @RequestParam String redirect_uri, HttpServletRequest request) {}

    @GetMapping("/providers")
    @Operation(summary = "Get all OAuth2 providers")
    public ResponseEntity<?> getProvidersList(){
        return ResponseEntity.ok(UserProviderType.values());
    }
}