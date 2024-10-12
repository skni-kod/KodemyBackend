package pl.sknikod.kodemyauth.infrastructure.module.auth;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemyauth.infrastructure.database.User;
import pl.sknikod.kodemyauth.infrastructure.module.auth.model.AuthInfo;
import pl.sknikod.kodemyauth.infrastructure.module.auth.model.AuthResponse;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.UserInfoResponse;

@Service
@RequiredArgsConstructor
public class AuthService {
    //private final JwtUtil jwtUtil;
    private final AuthMapper authMapper;

    public AuthInfo isAuthenticated() {
        AuthInfo authInfo = new AuthInfo();
        authInfo.setAuth(!SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .equals("anonymousUser")
        );
        return authInfo;
    }

    public AuthResponse getSessionToken() {
        /*String bearer = Option.of(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(UserPrincipal.class::isInstance)
                .map(o -> (UserPrincipal) o)
                .map(user -> jwtUtil.generateToken(new JwtUtil.Input(
                        user.getId(),
                        user.getUsername(),
                        user.getAuthorities()
                )))
                .map(JwtUtil.Output::getBearer)
                .getOrNull();
        return new AuthResponse(bearer);*/
        return null;
    }

    @Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
    public interface AuthMapper {
        UserInfoResponse map(User user);
    }
}