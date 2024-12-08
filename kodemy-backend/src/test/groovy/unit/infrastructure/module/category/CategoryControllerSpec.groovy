package unit.infrastructure.module.category

import org.spockframework.util.Assert
import pl.sknikod.kodemybackend.infrastructure.module.category.CategoryController
import pl.sknikod.kodemybackend.infrastructure.module.category.CategoryService
import pl.sknikod.kodemybackend.infrastructure.module.category.model.SingleCategoryResponse
import pl.sknikod.kodemybackend.infrastructure.module.section.model.SingleSectionInfoResponse
import spock.lang.Specification

class CategoryControllerSpec extends Specification {
    def categoryService = Mock(CategoryService)

    def categoryController = new CategoryController(categoryService)

    def "shouldGetCategoryDetails"() {
        given:
        def response = new SingleCategoryResponse(1L, "name",
                new SingleSectionInfoResponse(1L, "name")
        )
        categoryService.showCategoryInfo(1L) >> response

        when:
        def result = categoryController.getCategoryDetails(1L)

        then:
        Assert.that(result.body == response)
    }
}
