package pl.sknikod.kodemyauth.infrastructure.module.auth.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemyauth.infrastructure.database.User;
import pl.sknikod.kodemyauth.infrastructure.store.UserStore;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.security.AuthFacade;
import pl.sknikod.kodemycommons.security.UserPrincipal;

@Service
@RequiredArgsConstructor
public class GetAuthService {
    private final UserStore userStore;

    public AuthInfoResponse getAuth() {
        return AuthFacade.getCurrentUserPrincipal()
                .map(UserPrincipal::getId)
                .map(id -> userStore.findById(id).getOrNull())
                .map(this::map)
                .orElseThrow(InternalError500Exception::new);
    }

    private AuthInfoResponse map(User user) {
        return new AuthInfoResponse(user.getId(), user.getUsername(), new AuthInfoResponse.RoleDetails(
                user.getRole().getId(), user.getRole().getName()
        ));
    }
}
