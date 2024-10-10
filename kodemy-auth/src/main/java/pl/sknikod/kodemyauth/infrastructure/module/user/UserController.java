package pl.sknikod.kodemyauth.infrastructure.module.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemyauth.infrastructure.database.model.Role;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.SearchFields;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.SimpleUserResponse;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.UserInfoResponse;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.UserSortField;
import pl.sknikod.kodemyauth.infrastructure.rest.UserControllerDefinition;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerDefinition {
    private final ChangeUserRoleUseCase changeUserRoleUseCase;
    private final UserService userService;
    private final UsersBriefUseCase usersBriefUseCase;

    @Override
    public ResponseEntity<Void> updateRoles(Long userId, String roleName) {
        changeUserRoleUseCase.change(userId, roleName);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Override
    public ResponseEntity<UserInfoResponse> getUserInfo(Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserInfo(userId));
    }

    @Override
    public ResponseEntity<UserInfoResponse> getCurrentUserInfo() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getCurrentUserInfo());
    }

    @Override
    public ResponseEntity<Page<UserInfoResponse>> searchUsers(
            int size,
            int page,
            UserSortField sortField,
            Sort.Direction sortDirection,
            SearchFields searchFields
    ) {
        var pageRequest = PageRequest.of(page, size, sortDirection, sortField.getField());
        var searchFieldsParam = Objects.isNull(searchFields) ? new SearchFields() : searchFields;
        return ResponseEntity.status(HttpStatus.OK).body(
                userService.searchUsers(pageRequest, searchFieldsParam)
        );
    }

    @Override
    public ResponseEntity<List<SimpleUserResponse>> getUsersBrief(Set<Long> ids) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(usersBriefUseCase.getUserBrief(ids));
    }
}
