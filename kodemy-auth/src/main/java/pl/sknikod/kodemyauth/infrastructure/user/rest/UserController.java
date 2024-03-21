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

import java.util.Objects;

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
    public ResponseEntity<Page<UserInfoResponse>> searchUsers(
            int size,
            int page,
            PossibleUserSortFields sort,
            Sort.Direction sortDirection,
            SearchFields searchFields
    ) {
        var pageRequest = PageRequest.of(page, size, sortDirection, sort.toString());
        var searchFieldsParam = Objects.isNull(searchFields) ? new SearchFields() : searchFields;
        return ResponseEntity.status(HttpStatus.OK).body(
                userService.searchUsers(pageRequest, searchFieldsParam)
        );
    }
}
