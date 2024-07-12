package pl.sknikod.kodemybackend.factory;

import pl.sknikod.kodemybackend.infrastructure.database.entity.Category;

public class CategoryFactory {
    private CategoryFactory() {
    }

    public static Category category(Long id) {
        var category = new Category();
        category.setId(id);
        category.setSignature("AW_Frontend Dev");
        category.setName("Frontend Dev");
        category.setSection(SectionFactory.section());
        return category;
    }
}
