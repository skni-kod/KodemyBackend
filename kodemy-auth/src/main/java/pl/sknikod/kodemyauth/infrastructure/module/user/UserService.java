package pl.sknikod.kodemyauth.infrastructure.module.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemyauth.infrastructure.database.handler.RoleStoreHandler;
import pl.sknikod.kodemyauth.infrastructure.database.handler.UserStoreHandler;
import pl.sknikod.kodemyauth.infrastructure.database.model.Role;
import pl.sknikod.kodemyauth.infrastructure.database.model.User;
import pl.sknikod.kodemyauth.infrastructure.module.auth.AuthService;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.SearchFields;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.UserInfoResponse;
import pl.sknikod.kodemycommon.exception.InternalError500Exception;
import pl.sknikod.kodemycommon.security.AuthFacade;
import pl.sknikod.kodemycommon.security.UserPrincipal;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final RoleStoreHandler roleStoreHandler;
    private final UserStoreHandler userStoreHandler;
    private final AuthService.AuthMapper authMapper;

    public static boolean checkPrivilege(String privilege) {
        return AuthFacade.getCurrentUserPrincipal()
                .map(UserPrincipal::getAuthorities)
                .map(HashSet::new)
                .map(perms -> perms.contains(new SimpleGrantedAuthority(privilege)))
                .orElse(false);
    }

    @Transactional
    public UserInfoResponse getUserInfo(Long userId) {
        return userStoreHandler.findById(userId)
                .map(userMapper::map)
                .getOrElseThrow(th -> th instanceof InternalError500Exception ex
                        ? ex : new InternalError500Exception());
    }

    public UserInfoResponse getCurrentUserInfo() {
        return AuthFacade.getCurrentUserPrincipal()
                .map(UserPrincipal::getId)
                .map(id -> userStoreHandler.findById(id).getOrNull())
                .map(userMapper::map)
                .orElseThrow(InternalError500Exception::new);
    }

    public Page<UserInfoResponse> searchUsers(PageRequest pageRequest, SearchFields searchFields) {
        Page<User> users = userStoreHandler.findByUsernameOrEmailOrRole(
                searchFields.getUsername(),
                searchFields.getEmail(),
                searchFields.getRole() != null ? searchFields.getRole().name() : null,
                pageRequest
        );
        return new PageImpl<>(
                users.getContent().parallelStream().map(authMapper::map).toList(),
                pageRequest,
                users.getTotalElements()
        );
    }

    @Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
    public interface RoleMapper {
        UserInfoResponse.RoleDetails map(Role role);
    }

    @Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {RoleMapper.class})
    public interface UserMapper {
        UserInfoResponse map(User user);
    }
}
