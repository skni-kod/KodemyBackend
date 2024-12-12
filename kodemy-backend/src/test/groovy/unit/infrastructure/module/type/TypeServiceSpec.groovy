package unit.infrastructure.module.type

import io.vavr.control.Try
import org.spockframework.util.Assert
import pl.sknikod.kodemybackend.infrastructure.database.Type
import pl.sknikod.kodemybackend.infrastructure.mapper.TypeMapper
import pl.sknikod.kodemybackend.infrastructure.module.type.TypeService
import pl.sknikod.kodemybackend.infrastructure.module.type.model.SingleTypeResponse
import pl.sknikod.kodemybackend.infrastructure.store.TypeStore
import spock.lang.Specification

class TypeServiceSpec extends Specification {
    def typeStore = Mock(TypeStore)
    def typeMapper = Mock(TypeMapper)

    def typeService = new TypeService(typeStore, typeMapper)

    def "shouldGetAllTypes"() {
        given:
        def type = new Type()
        type.id = 1
        type.name = "name"
        def typeResponse = new SingleTypeResponse(
                1L, "name"
        )
        typeStore.findAll() >> Try.success(new ArrayList<>(List.of(type)))
        typeMapper.map(_ as List) >> new ArrayList<>(List.of(typeResponse))

        when:
        def result = typeService.getAllTypes()

        then:
        Assert.that(result == List.of(typeResponse))
    }
}
