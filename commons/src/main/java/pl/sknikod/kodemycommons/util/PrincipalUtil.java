package pl.sknikod.kodemycommons.util;

import org.springframework.stereotype.Component;
import pl.sknikod.kodemycommons.security.AuthFacade;
import pl.sknikod.kodemycommons.security.UserPrincipal;

import java.util.Optional;

@Component
public class PrincipalUtil {
    public Optional<UserPrincipal> getPrincipal(){
        return AuthFacade.getCurrentUserPrincipal();
    }
}
