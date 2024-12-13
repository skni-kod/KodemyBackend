package pl.sknikod.kodemysearch.module.material

import io.vavr.control.Try
import org.opensearch.client.opensearch.core.UpdateResponse
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialGradeService
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialAvgGradeEvent
import pl.sknikod.kodemysearch.infrastructure.store.MaterialSearchStore
import spock.lang.Specification
import spock.lang.Subject

class MaterialGradeServiceSpec extends Specification {

    MaterialSearchStore materialSearchStore = Mock()

    @Subject
    MaterialGradeService materialGradeService = new MaterialGradeService(materialSearchStore)

    def "should update average grade successfully"() {
        given: "an event with material ID and average grade"
            def event = new MaterialAvgGradeEvent()
            event.setId(1L)
            event.setAvgGrade(4.5f)

        and: "MaterialSearchStore successfully updates the grade"
            materialSearchStore.update(event.id, event.avgGrade) >> Try.success(Mock(UpdateResponse))

        when: "updateAvgGrade is called"
            materialGradeService.updateAvgGrade(event)

        then: "the update method is called with correct arguments"
            1 * materialSearchStore.update(1L, 4.5f)
    }
}
