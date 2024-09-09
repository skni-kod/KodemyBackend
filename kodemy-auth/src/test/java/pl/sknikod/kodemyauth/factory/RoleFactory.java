package pl.sknikod.kodemyauth.factory;

import pl.sknikod.kodemyauth.infrastructure.database.model.Role;

public class RoleFactory {
    private RoleFactory() {
    }

    public static Role roleUser = role(Role.RoleName.ROLE_USER);
    public static Role roleAdmin = role(Role.RoleName.ROLE_ADMIN);

    public static Role role(Role.RoleName roleEnum){
        Role role = new Role();
        role.setId(1L);
        role.setName(roleEnum);
        return role;
    }
}
