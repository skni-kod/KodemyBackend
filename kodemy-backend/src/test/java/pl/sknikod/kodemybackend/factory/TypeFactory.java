package pl.sknikod.kodemybackend.factory;

import pl.sknikod.kodemybackend.infrastructure.database.entity.Type;

public class TypeFactory {
    private TypeFactory() {
    }

    public static Type type(){
        var type = new Type();
        type.setId(1L);
        return type;
    }
}
