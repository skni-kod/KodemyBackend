package unit.infrastructure.store

import org.spockframework.util.Assert
import pl.sknikod.kodemybackend.infrastructure.database.Section
import pl.sknikod.kodemybackend.infrastructure.database.SectionRepository
import pl.sknikod.kodemybackend.infrastructure.store.SectionStore
import spock.lang.Specification

class SectionStoreSpec extends Specification {
    def sectionRepository = Mock(SectionRepository)

    def sectionStore = new SectionStore(sectionRepository)

    def "shouldFindAllSections"() {
        given:
        def section = new Section()
        sectionRepository.findAllWithFetchCategories()
                >> new ArrayList<Section>(List.of(section))
        when:
        def result = sectionStore.findAll().get()
        then:
        Assert.that(result == List.of(section))
    }
}
