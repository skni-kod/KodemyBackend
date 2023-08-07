package pl.sknikod.kodemy.infrastructure.rest;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.structure.NotFoundException;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.model.entity.Role;
import pl.sknikod.kodemy.infrastructure.model.entity.RoleName;
import pl.sknikod.kodemy.infrastructure.model.entity.User;
import pl.sknikod.kodemy.infrastructure.model.entity.UserPrincipal;
import pl.sknikod.kodemy.infrastructure.model.repository.RoleRepository;
import pl.sknikod.kodemy.infrastructure.model.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;

import static pl.sknikod.kodemy.infrastructure.model.entity.RoleName.ROLE_USER;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

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

    public void changeRoles(Long userId, RoleName roleName) {
//        Role role = roleRepository.findByName(roleName).orElseThrow(()->new NotFoundException("Role not found."));
//        Role contextUserRole = getContextUser().getRole();
//        if (contextUserRole.getName().equals(ROLE_USER)) {
//            throw new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, User.class);
//        }
//        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
//        user.setRole(role);
//        userRepository.save(user);
        Role requestedRole = roleRepository
                .findByName(roleName)
                .orElseThrow(()->new NotFoundException("Role not found."));
        Optional.of(getContextUser().getRole())
                .filter(role -> role.getName().equals(ROLE_USER))
                .ifPresent(role -> {
                    throw new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, User.class);
                });
        Option.of(userId)
                .map(userRepository::findById)
                .map(Optional::get)
                .peek(user->user.setRole(requestedRole))
                .map(userRepository::save)
                .getOrElseThrow(()->new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, User.class));
    }

    public Role getUserRole(Long userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new NotFoundException(NotFoundException.Format.ENTITY_ID, User.class, userId)
                );
        return user.getRole();
    }

}
