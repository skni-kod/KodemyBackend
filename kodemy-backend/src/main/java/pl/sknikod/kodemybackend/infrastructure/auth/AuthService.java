package pl.sknikod.kodemybackend.infrastructure.auth;

import io.vavr.control.Try;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.configuration.SecurityConfig;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;

@Service
public class AuthService {
    public SecurityConfig.JwtUserDetails getPrincipal() {
        return Try.of(() -> SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .onFailure(th -> {
                    throw new ServerProcessingException();
                })
                .filter(principal -> principal instanceof SecurityConfig.JwtUserDetails)
                .map(SecurityConfig.JwtUserDetails.class::cast)
                .getOrElseThrow(() -> new ServerProcessingException());
    }
}
