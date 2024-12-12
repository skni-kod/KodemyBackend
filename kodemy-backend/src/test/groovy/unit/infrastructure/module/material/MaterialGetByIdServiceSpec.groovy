package unit.infrastructure.module.material

import factory.MaterialFactory
import io.vavr.control.Try
import pl.sknikod.kodemybackend.infrastructure.database.Material
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialGetByIdService
import pl.sknikod.kodemybackend.infrastructure.store.MaterialStore
import spock.lang.Specification

import java.time.LocalDateTime

class MaterialGetByIdServiceSpec extends Specification {
    def materialStore = Mock(MaterialStore)

    def materialGetByIdService = new MaterialGetByIdService(materialStore)

    def "shouldShowDetails"() {
        given:
        def time = LocalDateTime.now()
        def material = MaterialFactory.create()
        material.createdDate = time
        materialStore.findById(1L)
                >> Try.success(new MaterialStore.FindByIdObject(
                material, "name", 3.0D,
                new ArrayList<Long>(List.of(3L))
        ))

        when:
        def result = materialGetByIdService.showDetails(1L)

        then:
        verifyAll {
            result.id == 1L
            result.title == "title"
            result.description == "desc"
            result.link == "link"
            result.status == Material.MaterialStatus.APPROVED
            result.type.id == material.type.id
        }
    }
}
