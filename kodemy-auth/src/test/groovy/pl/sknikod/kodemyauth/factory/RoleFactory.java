package pl.sknikod.kodemyauth.factory;

import pl.sknikod.kodemyauth.infrastructure.database.Permission;
import pl.sknikod.kodemyauth.infrastructure.database.Role;

import java.util.Set;

public class RoleFactory {
    public static Role create() {
        Role role = new Role();
        role.setId(1L);
        role.setPermissions(Set.of(new Permission("PERMISSION")));
        return role;
    }
}
