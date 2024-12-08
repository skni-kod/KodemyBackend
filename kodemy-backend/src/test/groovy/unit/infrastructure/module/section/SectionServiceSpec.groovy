package unit.infrastructure.module.section

import io.vavr.control.Try
import org.spockframework.util.Assert
import pl.sknikod.kodemybackend.infrastructure.database.Section
import pl.sknikod.kodemybackend.infrastructure.mapper.SectionMapper
import pl.sknikod.kodemybackend.infrastructure.module.section.SectionService
import pl.sknikod.kodemybackend.infrastructure.module.section.model.SingleSectionResponse
import pl.sknikod.kodemybackend.infrastructure.store.SectionStore
import spock.lang.Specification

class SectionServiceSpec extends Specification {
    def sectionStore = Mock(SectionStore)
    def sectionMapper = Mock(SectionMapper)

    def sectionService = new SectionService(sectionStore, sectionMapper)

    def "shouldGetAllSections"() {
        given:
        def section = new Section()
        section.id = 1L
        section.name = "name"
        section.categories = new HashSet<>()
        def response = new SingleSectionResponse(
                1L, "name", new ArrayList<>()
        )
        sectionStore.findAll() >> Try.success(new ArrayList<>(List.of(new Section())))
        sectionMapper.map(_ as List) >> new ArrayList<>(List.of(response))

        when:
        def result = sectionService.getAllSections()

        then:
        Assert.that(result == List.of(response))
    }
}
