package pl.sknikod.kodemyauth.infrastructure.module.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemyauth.exception.structure.ServerProcessingException;
import pl.sknikod.kodemyauth.infrastructure.database.entity.Role;
import pl.sknikod.kodemyauth.infrastructure.database.entity.User;
import pl.sknikod.kodemyauth.infrastructure.database.handler.RoleRepositoryHandler;
import pl.sknikod.kodemyauth.infrastructure.database.handler.UserRepositoryHandler;
import pl.sknikod.kodemyauth.infrastructure.module.common.mapper.UserMapper;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.SearchFields;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.UserInfoResponse;
import pl.sknikod.kodemyauth.util.auth.AuthFacade;
import pl.sknikod.kodemyauth.util.auth.UserPrincipal;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final RoleRepositoryHandler roleRepositoryHandler;
    private final UserRepositoryHandler userRepositoryHandler;

    public static boolean checkPrivilege(String privilege) {
        return AuthFacade.getCurrentUserPrincipal()
                .map(UserPrincipal::getAuthorities)
                .map(HashSet::new)
                .map(perms -> perms.contains(new SimpleGrantedAuthority(privilege)))
                .orElse(false);
    }

    @Transactional
    public UserInfoResponse getUserInfo(Long userId) {
        return userRepositoryHandler.findById(userId)
                .map(userMapper::map)
                .getOrElseThrow(th -> th instanceof ServerProcessingException ex
                        ? ex : new ServerProcessingException());
    }

    public UserInfoResponse getCurrentUserInfo() {
        return AuthFacade.getCurrentUserPrincipal()
                .map(UserPrincipal::getId)
                .map(id -> userRepositoryHandler.findById(id).getOrNull())
                .map(userMapper::map)
                .orElseThrow(ServerProcessingException::new);
    }

    public Page<UserInfoResponse> searchUsers(PageRequest pageRequest, SearchFields searchFields) {
        Role role = roleRepositoryHandler.findByRoleName(searchFields.getRole().name())
                .getOrNull();
        Page<User> users = userRepositoryHandler.findByUsernameOrEmailOrRole(
                searchFields.getUsername(), searchFields.getEmail(), role, pageRequest
        );
        return new PageImpl<>(
                users.getContent().parallelStream().map(userMapper::map).toList(),
                pageRequest,
                users.getTotalElements()
        );
    }
}
