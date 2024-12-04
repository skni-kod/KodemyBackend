package pl.sknikod.kodemyauth.factory;

import pl.sknikod.kodemyauth.infrastructure.database.User;

public class UserFactory {
    public static User create(Long userId) {
        User user = new User(
                "username",
                "email@email.com",
                null,
                RoleFactory.create()
        );
        user.setId(userId);
        return user;
    }

    public static User create() {
        return create(1L);
    }
}
