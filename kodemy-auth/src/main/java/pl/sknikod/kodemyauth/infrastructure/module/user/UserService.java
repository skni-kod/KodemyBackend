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
import pl.sknikod.kodemyauth.infrastructure.dao.RoleDao;
import pl.sknikod.kodemyauth.infrastructure.dao.UserDao;
import pl.sknikod.kodemyauth.infrastructure.database.Role;
import pl.sknikod.kodemyauth.infrastructure.database.User;
import pl.sknikod.kodemyauth.infrastructure.module.auth.AuthService;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.FilterSearchParams;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.UserInfoResponse;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.security.AuthFacade;
import pl.sknikod.kodemycommons.security.UserPrincipal;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final RoleDao roleDao;
    private final UserDao userDao;
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
        return userDao.findById(userId)
                .map(userMapper::map)
                .getOrElseThrow(th -> th instanceof InternalError500Exception ex
                        ? ex : new InternalError500Exception());
    }

    public UserInfoResponse getCurrentUserInfo() {
        return AuthFacade.getCurrentUserPrincipal()
                .map(UserPrincipal::getId)
                .map(id -> userDao.findById(id).getOrNull())
                .map(userMapper::map)
                .orElseThrow(InternalError500Exception::new);
    }

    public Page<UserInfoResponse> searchUsers(PageRequest pageRequest, FilterSearchParams filterSearchParams) {
        Page<User> users = userDao.findByUsernameOrEmailOrRole(
                filterSearchParams.getUsername(),
                filterSearchParams.getEmail(),
                filterSearchParams.getRoleName() != null ? filterSearchParams.getRoleName() : null,
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
