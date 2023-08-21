package pl.sknikod.kodemy.infrastructure.rest

import pl.sknikod.kodemy.MvcIntegrationSpec
import pl.sknikod.kodemy.infrastructure.model.entity.Type
import pl.sknikod.kodemy.infrastructure.model.repository.TypeRepository
import pl.sknikod.kodemy.infrastructure.rest.mapper.TypeMapper
import pl.sknikod.kodemy.infrastructure.rest.model.SingleTypeResponse
import spock.lang.Specification

class TypeServiceSpec extends Specification {

    def typeRepository = Mock(TypeRepository)
    def typeMapper = Mock(TypeMapper)
    def typeService = new TypeService(typeRepository, typeMapper)

    def "should return list of all types"() {
        given:
        def response = List.of(
                new SingleTypeResponse(1L, "Nazwa"),
                new SingleTypeResponse(2L, "Nazwa 2")
        )

        typeRepository.findAll() >> Collections.emptyList()
        typeMapper.map(_ as List<Type>) >> response

        when:
        def result = typeService.getAllTypes()

        then:
        result == response
    }
}
