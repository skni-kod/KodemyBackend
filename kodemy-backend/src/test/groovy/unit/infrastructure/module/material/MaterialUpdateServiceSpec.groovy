package unit.infrastructure.module.material

import factory.MaterialFactory
import io.vavr.control.Try
import org.spockframework.util.Assert
import org.springframework.security.core.authority.SimpleGrantedAuthority
import pl.sknikod.kodemybackend.infrastructure.database.Material
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialUpdateService
import pl.sknikod.kodemybackend.infrastructure.store.CategoryStore
import pl.sknikod.kodemybackend.infrastructure.store.MaterialStore
import pl.sknikod.kodemybackend.infrastructure.store.TagStore
import pl.sknikod.kodemybackend.infrastructure.store.TypeStore
import pl.sknikod.kodemycommons.security.UserPrincipal
import pl.sknikod.kodemycommons.util.PrincipalUtil
import spock.lang.Specification

class MaterialUpdateServiceSpec extends Specification {
    def updateMaterialMapper = Mock(MaterialUpdateService.MaterialUpdateMapper)
    def categoryStore = Mock(CategoryStore)
    def typeStore = Mock(TypeStore)
    def tagStore = Mock(TagStore)
    def materialStore = Mock(MaterialStore)
    def principalUtil = Mock(PrincipalUtil)

    def material = MaterialFactory.create()

    def materialUpdateService = new MaterialUpdateService(
            updateMaterialMapper,
            categoryStore,
            typeStore,
            tagStore,
            materialStore,
            principalUtil
    )

    def "shouldUpdate"() {
        given:
        materialStore.findById(1L, true)
                >> Try.success(new MaterialStore.FindByIdObject(
                material, "user", 3.0D, new ArrayList<Long>()
        ))
        categoryStore.findById(1L) >> Try.success(material.category)
        typeStore.findById(1L) >> Try.success(material.type)
        tagStore.findAllByIdIn(List.of(1L)) >> Try.success(material.tags)
        materialStore.update(_ as Material) >> Try.success(material)
        def response = new MaterialUpdateService.MaterialUpdateResponse(
                material.id, material.title,
                material.description, material.link,
                new MaterialUpdateService.MaterialUpdateResponse.BaseDetails(
                        material.category.id, material.category.name
                ),
                new MaterialUpdateService.MaterialUpdateResponse.BaseDetails(
                        material.type.id, material.type.name
                ),
                Set.of(new MaterialUpdateService.MaterialUpdateResponse.BaseDetails(
                        1L, "name"
                )))
        updateMaterialMapper.map(_ as Material)
                >> response
        principalUtil.getPrincipal() >> Optional.of(new UserPrincipal(
                1L, "name", List.of(new SimpleGrantedAuthority(
                "CAN_AUTO_APPROVED_MATERIAL"
        )))
        )

        when:
        def result = materialUpdateService.update(1L,
                new MaterialUpdateService.MaterialUpdateRequest(
                        "title", "desc", "link", 1L, 1L, List.of(1L)
                ))

        then:
        Assert.that(result == response)
    }
}
