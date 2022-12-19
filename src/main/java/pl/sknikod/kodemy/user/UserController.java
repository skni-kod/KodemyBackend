package pl.sknikod.kodemy.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {
    
    private final UserService userService;
}