package unit.infrastructure.module.material

import factory.MaterialFactory
import io.vavr.control.Try
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import pl.sknikod.kodemybackend.infrastructure.event.producer.MaterialUpdatedProducer
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialIndexService
import pl.sknikod.kodemybackend.infrastructure.store.MaterialStore
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDateTime

class MaterialIndexServiceSpec extends Specification {
    def materialStore = Mock(MaterialStore)
    def materialUpdaterProducer = Mock(MaterialUpdatedProducer)

    def materialIndexService = new MaterialIndexService(materialStore, materialUpdaterProducer)

    def "shouldReindex"() {
        given:
        def material = MaterialFactory.create()
        materialStore.findAllInDateRange(
                _ as LocalDateTime, _ as LocalDateTime, _ as PageRequest
        ) >> Try.success(new PageImpl<>(List.of(
                new MaterialStore.FindAllPageWithUserObject(
                        material,
                        3.0D,
                        "name"
                )
        )))
        materialUpdaterProducer.publish(_ as MaterialUpdatedProducer.Message) >> _

        when:
        materialIndexService.reindex(Instant.now(), Instant.now().plusSeconds(10))

        then:
        noExceptionThrown()
    }
}
