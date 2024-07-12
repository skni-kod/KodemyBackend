package pl.sknikod.kodemybackend.factory;

import pl.sknikod.kodemybackend.infrastructure.database.entity.Section;

public class SectionFactory {
    private SectionFactory() {
    }

    public static Section section() {
        var section = new Section();
        section.setId(1L);
        section.setName("Aplikacje webowe");
        section.setPrefix("AW");
        return section;
    }
}
