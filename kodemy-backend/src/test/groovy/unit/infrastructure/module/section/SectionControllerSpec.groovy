package unit.infrastructure.module.section

import org.spockframework.util.Assert
import pl.sknikod.kodemybackend.infrastructure.module.section.SectionController
import pl.sknikod.kodemybackend.infrastructure.module.section.SectionService
import pl.sknikod.kodemybackend.infrastructure.module.section.model.SingleSectionResponse
import spock.lang.Specification

class SectionControllerSpec extends Specification {
    def sectionService = Mock(SectionService)

    def sectionController = new SectionController(sectionService)

    def "shouldGetAll"() {
        given:
        def response = new SingleSectionResponse(
                1L, "name", new ArrayList<>()
        )
        sectionService.getAllSections() >> new ArrayList<>(List.of(response))

        when:
        def result = sectionController.getAll()

        then:
        Assert.that(result.body == List.of(response))
    }
}
