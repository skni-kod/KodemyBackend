package unit.infrastructure.module.material

import factory.MaterialFactory
import io.vavr.control.Try
import org.spockframework.util.Assert
import org.springframework.security.core.authority.SimpleGrantedAuthority
import pl.sknikod.kodemybackend.infrastructure.database.Material
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialCreateService
import pl.sknikod.kodemybackend.infrastructure.store.CategoryStore
import pl.sknikod.kodemybackend.infrastructure.store.MaterialStore
import pl.sknikod.kodemybackend.infrastructure.store.TagStore
import pl.sknikod.kodemybackend.infrastructure.store.TypeStore
import pl.sknikod.kodemycommons.security.UserPrincipal
import pl.sknikod.kodemycommons.util.PrincipalUtil
import spock.lang.Specification

class MaterialCreateServiceSpec extends Specification {
    def materialStore = Mock(MaterialStore)
    def typeStore = Mock(TypeStore)
    def materialCreateMapper = Mock(MaterialCreateService.MaterialCreateMapper)
    def categoryStore = Mock(CategoryStore)
    def tagStore = Mock(TagStore)
    def principalUtil = Mock(PrincipalUtil)

    def materialCreateService = new MaterialCreateService(
            materialStore,
            typeStore,
            materialCreateMapper,
            categoryStore,
            tagStore,
            principalUtil
    )

    def "shouldCreate"() {
        given:
        def material = MaterialFactory.create()
        categoryStore.findById(1L) >> Try.success(material.category)
        typeStore.findById(1L) >> Try.success(material.type)
        tagStore.findAllByIdIn(_ as List) >> Try.success(material.tags)
        materialStore.save(_ as Material) >> Try.success(material)
        principalUtil.getPrincipal() >> Optional.of(
                new UserPrincipal(1L, "user", new ArrayList<SimpleGrantedAuthority>())
        )
        def response = new MaterialCreateService.MaterialCreateResponse(
                1L, "title", Material.MaterialStatus.APPROVED
        )
        materialCreateMapper.map(material) >> response

        when:
        def result = materialCreateService.create(
                new MaterialCreateService.MaterialCreateRequest(
                        "title", "desc", "link", 1L, 1L, List.of(1L)
                )
        )

        then:
        Assert.that(result == response)
    }
}
