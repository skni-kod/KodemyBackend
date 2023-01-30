package pl.sknikod.kodemy.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sknikod.kodemy.user.UserProviderType;

@RestController
@RequestMapping("/api/oauth2")
public class AuthController {

    @GetMapping("/authorize/{provider}")
    public void authorize(@RequestParam String redirect){}

    @GetMapping("/providers")
    public ResponseEntity<?> getProvidersList(){
        return ResponseEntity.ok(UserProviderType.values());
    }
}