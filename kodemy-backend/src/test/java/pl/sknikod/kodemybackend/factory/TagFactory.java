package pl.sknikod.kodemybackend.factory;

import pl.sknikod.kodemybackend.infrastructure.database.Tag;

public class TagFactory {
    private TagFactory() {
    }

    public static Tag tag() {
        var tag = new Tag();
        tag.setId(1L);
        return tag;
    }

    public static Tag tag(String name) {
        var tag = new Tag();
        tag.setId(1L);
        tag.setName(name);
        return tag;
    }
}
