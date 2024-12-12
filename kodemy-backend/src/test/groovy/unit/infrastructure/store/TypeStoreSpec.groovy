package unit.infrastructure.store

import org.spockframework.util.Assert
import pl.sknikod.kodemybackend.infrastructure.database.Type
import pl.sknikod.kodemybackend.infrastructure.database.TypeRepository
import pl.sknikod.kodemybackend.infrastructure.store.TypeStore
import pl.sknikod.kodemycommons.exception.NotFound404Exception
import spock.lang.Specification

class TypeStoreSpec extends Specification {
    def typeRepository = Mock(TypeRepository)

    def type = new Type()

    def typeStore = new TypeStore(typeRepository)

    def "shouldFindById"() {
        given:
        typeRepository.findById(1L) >> Optional.of(type)

        when:
        def result = typeStore.findById(1L).get()

        then:
        Assert.that(result == type)
    }

    def "shouldThrowWhenFindById"() {
        given:
        typeRepository.findById(1L) >> Optional.empty()

        when:
        typeStore.findById(1L).get()

        then:
        thrown(NotFound404Exception)
    }

    def "shouldFindAll"() {
        given:
        typeRepository.findAll() >> new ArrayList<Type>(List.of(type))

        when:
        def result = typeStore.findAll().get()

        then:
        Assert.that(result == List.of(type))
    }
}
