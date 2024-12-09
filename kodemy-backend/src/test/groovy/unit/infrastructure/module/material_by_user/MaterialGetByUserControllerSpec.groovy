package unit.infrastructure.module.material_by_user


import org.spockframework.util.Assert
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import pl.sknikod.kodemybackend.infrastructure.database.Material
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialPageable
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialSortField
import pl.sknikod.kodemybackend.infrastructure.module.material_by_user.MaterialGetByUserService
import pl.sknikod.kodemybackend.infrastructure.module.material_by_user.MaterialGetByUserController
import pl.sknikod.kodemybackend.infrastructure.rest.UserControllerDefinition
import spock.lang.Specification

import java.time.LocalDateTime

class MaterialGetByUserControllerSpec extends Specification {
    def materialGetByUserService = Mock(MaterialGetByUserService)

    def userController = new MaterialGetByUserController(materialGetByUserService)

    def "shouldGetUsersMaterials"() {
        given:
        def materialPageable = new MaterialPageable(
                1L,
                "title",
                "description",
                "link",
                Material.MaterialStatus.APPROVED,
                new MaterialPageable.TypeDetails(1L, "name"),
                new ArrayList<MaterialPageable.TagDetails>(),
                3.0D,
                new MaterialPageable.AuthorDetails(1L, "name"),
                LocalDateTime.now()
        )
        materialGetByUserService.getPersonalMaterials(
                1L,
                new UserControllerDefinition.FilterSearchParams(),
                _ as PageRequest
        ) >> new PageImpl<>(List.of(materialPageable))

        when:
        def result = userController.usersMaterials(
                1L, 1, 1,
                MaterialSortField.ID, Sort.Direction.ASC,
                new UserControllerDefinition.FilterSearchParams()
        )

        then:
        Assert.that(result.body.content == List.of(materialPageable))
    }
}
