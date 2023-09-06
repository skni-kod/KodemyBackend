package pl.sknikod.kodemy.infrastructure.rest

import pl.sknikod.kodemy.infrastructure.common.entity.Section
import pl.sknikod.kodemy.infrastructure.common.repository.SectionRepository
import pl.sknikod.kodemy.infrastructure.section.SectionService
import pl.sknikod.kodemy.infrastructure.common.mapper.SectionMapper
import pl.sknikod.kodemy.infrastructure.section.rest.SingleSectionResponse
import spock.lang.Specification

class SectionServiceSpec extends Specification {

    def sectionRepository = Mock(SectionRepository)
    def sectionMapper = Mock(SectionMapper)
    def sectionService = new SectionService(sectionRepository, sectionMapper)

    def setup() {
        def section = new Section()
        section.setId(1L)
        section.setName("Name")
        def sectionResponse = List.of(
                new SingleSectionResponse(1L, section.getName(),
                        List.of(new SingleSectionResponse.CategoryDetails(1L, "Name"))
                )
        )

        sectionMapper.map(_ as List<Section>) >> sectionResponse
        sectionRepository.findAllWithFetchCategories() >> List.of(section)
    }

    def "should get all sections"() {
        given:

        when:
        def result = sectionService.getAllSections()

        then:
        result.get(0).getId() == 1L
        result.get(0).getName() == "Name"
    }
}
