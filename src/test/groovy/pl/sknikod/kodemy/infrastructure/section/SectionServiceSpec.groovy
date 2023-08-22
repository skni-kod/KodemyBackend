package pl.sknikod.kodemy.infrastructure.section

import pl.sknikod.kodemy.infrastructure.common.entity.Section
import pl.sknikod.kodemy.infrastructure.common.mapper.SectionMapper
import pl.sknikod.kodemy.infrastructure.common.repository.SectionRepository
import pl.sknikod.kodemy.infrastructure.section.rest.SingleSectionResponse
import spock.lang.Specification

class SectionServiceSpec extends Specification {

    def sectionRepository = Mock(SectionRepository)
    def sectionMapper = Mock(SectionMapper)
    def sectionService = new SectionService(sectionRepository, sectionMapper)

    def "should return list of all sections"() {
        given:
        def response = List.of(
                new SingleSectionResponse(1L, "Nazwa", Collections.emptyList()),
                new SingleSectionResponse(2L, "Nazwa 2", Collections.emptyList())
        )

        sectionRepository.findAllWithFetchCategories() >> Collections.emptyList()
        sectionMapper.map(_ as List<Section>) >> response

        when:
        def result = sectionService.getAllSections()

        then:
        result == response
    }
}
