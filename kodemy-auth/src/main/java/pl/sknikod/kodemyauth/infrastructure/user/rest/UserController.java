package pl.sknikod.kodemyauth.infrastructure.user.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Role;
import pl.sknikod.kodemyauth.infrastructure.user.UserService;

@RestController
@AllArgsConstructor
public class UserController implements UserControllerDefinition {

    private final UserService userService;

    @Override
    @PreAuthorize("isAuthenticated() and hasAuthority('CAN_ASSIGN_ROLES')")
    public void updateRoles(Long userId, Role.RoleName roleName) {
        userService.changeRoles(userId, roleName);
    }

    @Override
    @PreAuthorize("isAuthenticated() and hasAuthority('CAN_GET_USER_INFO')")
    public ResponseEntity<UserInfoResponse> getUserInfo(Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserInfo(userId));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserInfoResponse> getCurrentUserInfo() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getCurrentUserInfo());
    }

    @Override
    public ResponseEntity<Page<UserInfoResponse>> searchForUser(
            int size, int page, String sort, Sort.Direction sortDirection,
            String username, String email, Role.RoleName role, String phrase
    ) {
        var pageRequest = PageRequest.of(page, size, sortDirection, sort);
        return ResponseEntity.status(HttpStatus.OK).body(userService.searchForUser(pageRequest, username, email, role, phrase));
    }

}
