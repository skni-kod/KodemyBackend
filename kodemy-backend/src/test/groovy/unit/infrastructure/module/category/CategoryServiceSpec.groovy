package unit.infrastructure.module.category

import io.vavr.control.Try
import org.spockframework.util.Assert
import pl.sknikod.kodemybackend.infrastructure.database.Category
import pl.sknikod.kodemybackend.infrastructure.mapper.CategoryMapper
import pl.sknikod.kodemybackend.infrastructure.module.category.CategoryService
import pl.sknikod.kodemybackend.infrastructure.module.category.model.SingleCategoryResponse
import pl.sknikod.kodemybackend.infrastructure.module.section.model.SingleSectionInfoResponse
import pl.sknikod.kodemybackend.infrastructure.store.CategoryStore
import spock.lang.Specification

class CategoryServiceSpec extends Specification {
    def categoryStore = Mock(CategoryStore)
    def categoryMapper = Mock(CategoryMapper)

    def categoryService = new CategoryService(categoryStore, categoryMapper)

    def "shouldShowCategoryInfo"() {
        given:
        def category = new Category()
        category.id = 1L
        category.name = 1L
        def response = new SingleCategoryResponse(
                1L,
                "name",
                new SingleSectionInfoResponse(1L, "name")
        )
        categoryStore.findById(1L) >> Try.success(category)
        categoryMapper.map(_ as Category) >> response

        when:
        def result = categoryService.showCategoryInfo(1L)

        then:
        Assert.that(result == response)
    }
}
