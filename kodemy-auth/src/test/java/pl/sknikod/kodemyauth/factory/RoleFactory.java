package pl.sknikod.kodemyauth.factory;

import pl.sknikod.kodemyauth.infrastructure.database.model.Role;

public class RoleFactory {
    private RoleFactory() {
    }

    public static Role roleUser = role("ROLE_USER");
    public static Role roleAdmin = role("ROLE_ADMIN");

    public static Role role(String roleName){
        Role role = new Role();
        role.setId(1L);
        role.setName(roleName);
        return role;
    }
}
