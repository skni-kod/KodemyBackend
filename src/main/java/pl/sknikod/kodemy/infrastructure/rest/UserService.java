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
        User user = Option.ofOptional(userRepository.findById(userId))
                .getOrElseThrow(()->
                    new NotFoundException(NotFoundException.Format.ENTITY_ID, User.class, userId)
                );
        Option.ofOptional(roleRepository.findByName(roleName))
                .onEmpty(()->{
                        throw new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Role.class);
                })
                .map(role -> {
                    user.setRole(role);
                    return user;
                })
                .map(userRepository::save)
                .onEmpty(()->{
                    throw new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, User.class);
                });
    }

}
