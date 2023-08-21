package pl.sknikod.kodemy.infrastructure.rest


import pl.sknikod.kodemy.exception.structure.AlreadyExistsException
import pl.sknikod.kodemy.infrastructure.model.entity.Technology
import pl.sknikod.kodemy.infrastructure.model.repository.TechnologyRepository
import pl.sknikod.kodemy.infrastructure.rest.mapper.TechnologyMapper
import pl.sknikod.kodemy.infrastructure.rest.model.TechnologyAddRequest
import pl.sknikod.kodemy.infrastructure.rest.model.TechnologyAddResponse
import spock.lang.Specification

class TechnologyServiceSpec extends Specification {

    def technologyRepository = Mock(TechnologyRepository)
    def technologyMapper = Mock(TechnologyMapper)
    def technologyService = new TechnologyService(technologyRepository, technologyMapper)

    def setup() {
        def technology = new Technology()
        technology.setId(1L)
        technology.setName("Nazwa")

        def technologyResponse = new TechnologyAddResponse()
        technologyResponse.setId(1L)
        technologyResponse.setName(technology.getName())

        technologyMapper.map(_ as TechnologyAddRequest) >> technology
        technologyMapper.map(_ as Technology) >> technologyResponse
        technologyRepository.save(_ as Technology) >> technology
    }

    def "should add technology"() {
        given:
        def request = new TechnologyAddRequest()
        request.setName("")

        technologyRepository.findByName(_ as String) >> Optional.empty()

        when:
        def result = technologyService.addTechnology(request)

        then:
        result.getId() == 1L
        result.getName() == "Nazwa"
    }

    def "should throw AlreadyExistsException when technology name exists"() {
        given:
        def request = new TechnologyAddRequest()
        request.setName("")
        technologyRepository.findByName(_ as String) >> Optional.of(new Technology())

        when:
        technologyService.addTechnology(request)

        then:
        thrown(AlreadyExistsException)
    }
}
