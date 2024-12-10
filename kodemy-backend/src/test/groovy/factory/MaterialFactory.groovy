package factory

import pl.sknikod.kodemybackend.infrastructure.database.Category
import pl.sknikod.kodemybackend.infrastructure.database.Material
import pl.sknikod.kodemybackend.infrastructure.database.Tag
import pl.sknikod.kodemybackend.infrastructure.database.Type

class MaterialFactory {
    static Material create() {
        def category = new Category()
        category.id = 1L
        def type = new Type()
        type.id = 1L
        def tags = new HashSet(List.of(new Tag("name")))
        def material = new Material()
        material.id = 1L
        material.title = "title"
        material.description = "desc"
        material.link = "link"
        material.status = Material.MaterialStatus.APPROVED
        material.category = category
        material.type = type
        material.tags = tags
        return material
    }
}
