package unit.infrastructure.module.material_by_user

import io.vavr.Tuple2
import io.vavr.control.Try
import org.spockframework.util.Assert
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import pl.sknikod.kodemybackend.infrastructure.database.Material
import pl.sknikod.kodemybackend.infrastructure.database.Type
import pl.sknikod.kodemybackend.infrastructure.module.material_by_user.MaterialGetByUserService
import pl.sknikod.kodemybackend.infrastructure.rest.UserControllerDefinition
import pl.sknikod.kodemybackend.infrastructure.store.MaterialStore
import pl.sknikod.kodemybackend.infrastructure.store.UserStore
import spock.lang.Specification

class MaterialGetByUserServiceSpec extends Specification {
    def materialStore = Mock(MaterialStore)
    def materialGetByUserService = new MaterialGetByUserService(materialStore)

    def "shouldGetPersonalMaterials"() {
        given:
        def material = new Material()
        def type = new Type()
        type.id = 1L
        material.type = type
        materialStore.findAll(1L, _ as MaterialStore.FindAllFilters)
                >> Try.success(new Tuple2(new PageImpl<>(List.of(
                new MaterialStore.FindAllPageObject(material, 3.0)
        )), new UserStore.User()))

        when:
        def result = materialGetByUserService.getPersonalMaterials(
                1L,
                new UserControllerDefinition.FilterSearchParams(),
                PageRequest.of(1, 1)
        )

        then:
        Assert.that(result.content.size() == 1)
    }

}
