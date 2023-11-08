package pl.sknikod.kodemyauth.infrastructure.user;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemyauth.exception.structure.NotFoundException;
import pl.sknikod.kodemyauth.exception.structure.ServerProcessingException;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Role;
import pl.sknikod.kodemyauth.infrastructure.common.entity.User;
import pl.sknikod.kodemyauth.infrastructure.common.entity.UserPrincipal;
import pl.sknikod.kodemyauth.infrastructure.common.mapper.UserMapper;
import pl.sknikod.kodemyauth.infrastructure.common.repository.RoleRepository;
import pl.sknikod.kodemyauth.infrastructure.common.repository.UserRepository;
import pl.sknikod.kodemyauth.infrastructure.user.rest.SearchFields;
import pl.sknikod.kodemyauth.infrastructure.user.rest.UserInfoResponse;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public User getContextUser() {
        return Option.of(UserService.getContextUserPrincipal())
                .map(UserPrincipal::getId)
                .map(userRepository::findById)
                .map(Optional::get)
                .getOrElseThrow(() -> new NotFoundException("Session user not found"));
    }

    public static boolean checkPrivilege(String privilege) {
        return Option.of(getContextUserPrincipal())
                .map(UserPrincipal::getAuthorities)
                .map(HashSet::new)
                .map(perms -> perms.contains(new SimpleGrantedAuthority(privilege)))
                .getOrElse(false);
    }

    public static UserPrincipal getContextUserPrincipal() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(auth -> (UserPrincipal) auth.getPrincipal())
                .orElse(null);
    }

    public void changeRoles(Long userId, Role.RoleName roleName) {
        User user = Option.ofOptional(userRepository.findById(userId))
                .getOrElseThrow(() ->
                        new NotFoundException(NotFoundException.Format.ENTITY_ID, User.class, userId)
                );
        Option.ofOptional(roleRepository.findByName(roleName))
                .onEmpty(() -> {
                    throw new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Role.class);
                })
                .map(role -> {
                    user.setRole(role);
                    return user;
                })
                .map(userRepository::save)
                .onEmpty(() -> {
                    throw new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, User.class);
                });
    }

    @Transactional
    public UserInfoResponse getUserInfo(Long userId) {
        return Option.ofOptional(userRepository.findById(userId))
                .map(userMapper::map)
                .getOrElseThrow(() ->
                        new NotFoundException(NotFoundException.Format.ENTITY_ID, User.class, userId)
                );
    }

    public UserInfoResponse getCurrentUserInfo() {
        return Option
                .of(UserService.getContextUserPrincipal())
                .map(UserPrincipal::getId)
                .map(userRepository::findById)
                .map(Optional::get)
                .map(userMapper::map)
                .getOrElseThrow(() -> {
                    throw new ServerProcessingException();
                });
    }

    public Page<UserInfoResponse> searchUsers(PageRequest pageRequest, SearchFields searchFields) {
        Role role = Option.ofOptional(roleRepository.findByName(searchFields.getRole()))
                .getOrNull();
        Page<User> users = userRepository.findByUsernameOrEmailOrRole(
                searchFields.getUsername(), searchFields.getEmail(), role, pageRequest
        );
        return new PageImpl<>(
                users.getContent().parallelStream().map(userMapper::map).toList(),
                pageRequest,
                users.getTotalElements()
        );
    }
}
