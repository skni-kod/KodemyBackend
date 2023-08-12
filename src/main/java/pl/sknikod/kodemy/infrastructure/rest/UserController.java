package pl.sknikod.kodemy.infrastructure.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.infrastructure.model.entity.RoleName;
import pl.sknikod.kodemy.infrastructure.rest.model.UserInfoResponse;

@RestController
@AllArgsConstructor
public class UserController implements UserControllerDefinition {

    private final UserService userService;

    @Override
    @PreAuthorize("isAuthenticated() and hasAuthority('CAN_ASSIGN_ROLES')")
    public void updateRoles(Long userId, RoleName roleName) {
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

}
