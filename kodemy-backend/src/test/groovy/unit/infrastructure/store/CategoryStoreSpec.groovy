package unit.infrastructure.store

import org.spockframework.util.Assert
import pl.sknikod.kodemybackend.infrastructure.database.Category
import pl.sknikod.kodemybackend.infrastructure.database.CategoryRepository
import pl.sknikod.kodemybackend.infrastructure.store.CategoryStore
import pl.sknikod.kodemycommons.exception.NotFound404Exception
import spock.lang.Specification

class CategoryStoreSpec extends Specification {
    def categoryRepository = Mock(CategoryRepository)

    def categoryStore = new CategoryStore(categoryRepository)

    private static final CATEGORY_ID = 1L

    def "shouldFindCategoryById"() {
        given:
        categoryRepository.findById(CATEGORY_ID) >> Optional.of(new Category())

        when:
        def result = categoryStore.findById(CATEGORY_ID)

        then:
        Assert.that(!result.isEmpty())
    }

    def "shouldThrowNotFoundWhenFindCategoryById"() {
        given:
        categoryRepository.findById(CATEGORY_ID) >> Optional.empty()

        when:
        categoryStore.findById(CATEGORY_ID).get()

        then:
        thrown(NotFound404Exception)
    }
}
