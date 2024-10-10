package pl.sknikod.kodemyauth.factory;

import pl.sknikod.kodemyauth.infrastructure.database.Role;
import pl.sknikod.kodemyauth.infrastructure.database.User;
import pl.sknikod.kodemycommon.security.UserPrincipal;

import java.util.Collections;

public class UserFactory {
    private UserFactory() {
    }

    public static User userUser = user(RoleFactory.roleUser);
    public static User userAdmin = user(RoleFactory.roleAdmin);
    public static UserPrincipal userPrincipal = userPrincipal();

    public static User user(Role role) {
        var user = new User("username", "email@email.com", "photo", role);
        user.setId(1L);
        user.setUsername("user");
        return user;
    }


    private static UserPrincipal userPrincipal() {
        return new UserPrincipal(
                1L, "username",
                false, false, false, true,
                Collections.emptySet()
        );
    }

}
