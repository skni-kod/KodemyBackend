package unit.infrastructure.module.material

import factory.MaterialFactory
import io.vavr.Tuple2
import io.vavr.control.Try
import org.spockframework.util.Assert
import org.springframework.security.core.authority.SimpleGrantedAuthority
import pl.sknikod.kodemybackend.infrastructure.database.Material
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialStatusService
import pl.sknikod.kodemybackend.infrastructure.store.MaterialStore
import pl.sknikod.kodemycommons.security.UserPrincipal
import pl.sknikod.kodemycommons.util.PrincipalUtil
import spock.lang.Specification

class MaterialStatusServiceSpec extends Specification {
    def materialStore = Mock(MaterialStore)
    def principalUtil = Mock(PrincipalUtil)

    def material = MaterialFactory.create()

    def materialStatusService = new MaterialStatusService(materialStore, principalUtil)

    def "shouldUpdate"() {
        given:
        material.status = Material.MaterialStatus.PENDING
        materialStore.findById(1L, true)
                >> Try.success(new MaterialStore.FindByIdObject(
                material, "user", 3.0D, new ArrayList<Long>()
        ))
        materialStore.changeStatus(1L, Material.MaterialStatus.APPROVED)
                >> Try.success(new Tuple2<>(1L, Material.MaterialStatus.APPROVED))
        principalUtil.getPrincipal() >> Optional.of(new UserPrincipal(
                1L, "name", new ArrayList<SimpleGrantedAuthority>(
                List.of(new SimpleGrantedAuthority("CAN_APPROVED_MATERIAL"))
        )
        ))

        when:
        def result = materialStatusService.update(1L, Material.MaterialStatus.APPROVED)

        then:
        Assert.that(result == Material.MaterialStatus.APPROVED)
    }
}
