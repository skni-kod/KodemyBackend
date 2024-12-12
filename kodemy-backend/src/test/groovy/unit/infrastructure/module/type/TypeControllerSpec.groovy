package unit.infrastructure.module.type

import org.spockframework.util.Assert
import org.springframework.http.ResponseEntity
import pl.sknikod.kodemybackend.infrastructure.module.type.TypeController
import pl.sknikod.kodemybackend.infrastructure.module.type.TypeService
import pl.sknikod.kodemybackend.infrastructure.module.type.model.SingleTypeResponse
import spock.lang.Specification

class TypeControllerSpec extends Specification {
    def typeService = Mock(TypeService)

    def typeController = new TypeController(typeService)

    def "shouldGetAll"() {
        given:
        def typeResponse = new SingleTypeResponse(1L, "name")
        typeService.getAllTypes() >> new ArrayList<>(List.of(typeResponse))

        when:
        def result = typeController.getAll()

        then:
        Assert.that(result == ResponseEntity.ok(List.of(typeResponse)))
    }
}
