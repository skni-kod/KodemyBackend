package unit.infrastructure.module.material

import factory.MaterialFactory
import io.vavr.Tuple2
import io.vavr.control.Try
import org.spockframework.util.Assert
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialManageService
import pl.sknikod.kodemybackend.infrastructure.rest.MaterialControllerDefinition
import pl.sknikod.kodemybackend.infrastructure.store.MaterialStore
import pl.sknikod.kodemybackend.infrastructure.store.UserStore
import spock.lang.Specification

class MaterialManageServiceSpec extends Specification {
    def materialStore = Mock(MaterialStore)

    def materialManageService = new MaterialManageService(materialStore)

    def "shouldManage"() {
        given:
        def material = MaterialFactory.create()
        def user = new UserStore.User()
        user.id = 1L
        user.username = "name"
        materialStore.findAll(_ as MaterialStore.FindAllFilters)
                >> Try.success(new Tuple2<>(new PageImpl<>(List.of(
                new MaterialStore.FindAllPageObject(material, 3.0D)
        )), new HashSet<>(Set.of(user))))

        when:
        def result = materialManageService.manage(
                new MaterialControllerDefinition.MaterialFilterSearchParams(),
                PageRequest.of(1, 1)
        )

        then:
        Assert.that(result.content.size() == 1)
    }
}
