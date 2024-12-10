package unit.infrastructure.module.material


import org.spockframework.util.Assert
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import pl.sknikod.kodemybackend.infrastructure.database.Material
import pl.sknikod.kodemybackend.infrastructure.module.material.*
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialPageable
import pl.sknikod.kodemybackend.infrastructure.module.material.model.SingleMaterialResponse
import pl.sknikod.kodemybackend.infrastructure.module.material.model.StatusesToChangeResponse
import pl.sknikod.kodemybackend.infrastructure.rest.MaterialControllerDefinition
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDateTime

class MaterialControllerSpec extends Specification {
    def materialCreateService = Mock(MaterialCreateService)
    def materialUpdateService = Mock(MaterialUpdateService)
    def materialGetByIdService = Mock(MaterialGetByIdService)
    def materialIndexService = Mock(MaterialIndexService)
    def materialStatusService = Mock(MaterialStatusService)
    def materialManageService = Mock(MaterialManageService)

    def materialController = new MaterialController(
            materialCreateService,
            materialUpdateService,
            materialGetByIdService,
            materialIndexService,
            materialStatusService,
            materialManageService
    )

    def "shouldCreate"() {
        given:
        def response = new MaterialCreateService.MaterialCreateResponse(1L, "title",
                Material.MaterialStatus.APPROVED)
        materialCreateService.create(_ as MaterialCreateService.MaterialCreateRequest)
                >> response

        when:
        def result = materialController.create(
                new MaterialCreateService.MaterialCreateRequest("title", "desc",
                        "link", 1L, 1L, new ArrayList<Long>()
                ))

        then:
        Assert.that(result.body == response)
    }

    def "shouldUpdate"() {
        given:
        def response = new MaterialUpdateService.MaterialUpdateResponse(
                1L, "title",
                "desc", "link",
                new MaterialUpdateService.MaterialUpdateResponse.BaseDetails(1L, "name"),
                new MaterialUpdateService.MaterialUpdateResponse.BaseDetails(1L, "name"),
                new HashSet<MaterialUpdateService.MaterialUpdateResponse.BaseDetails>()
        )
        materialUpdateService.update(1L, _ as MaterialUpdateService.MaterialUpdateRequest)
                >> response

        when:
        def result = materialController.update(1L,
                new MaterialUpdateService.MaterialUpdateRequest("title", "desc",
                        "link", 1L, 1L, new ArrayList<Long>()
                ))

        then:
        Assert.that(result.body == response)
    }

    def "shouldUpdateStatus"() {
        given:
        materialStatusService.update(1L, Material.MaterialStatus.APPROVED)
                >> Material.MaterialStatus.APPROVED

        when:
        def result = materialController.updateStatus(1L, Material.MaterialStatus.APPROVED)

        then:
        Assert.that(result.body == Material.MaterialStatus.APPROVED)
    }

    def "shouldReindex"() {
        given:
        materialIndexService.reindex(_ as Instant, _ as Instant)
                >> _

        when:
        def result = materialController.reindex(Instant.now(), Instant.now().plusSeconds(60))

        then:
        Assert.that(result instanceof ResponseEntity)
    }

    def "shouldShowDetails"() {
        given:
        def response = new SingleMaterialResponse(
                1L, "title", "description", "link", Material.MaterialStatus.APPROVED,
                new SingleMaterialResponse.TypeDetails(1L, "name"),
                new ArrayList<SingleMaterialResponse.TagDetails>(),
                3.0D, new ArrayList<Long>(), new SingleMaterialResponse.AuthorDetails(1L, "name"),
                LocalDateTime.now()
        )
        materialGetByIdService.showDetails(1L)
                >> response

        when:
        def result = materialController.showDetails(1L)

        then:
        Assert.that(result.body == response)
    }

    def "shouldShowStatusesToChange"() {
        given:
        def response = new StatusesToChangeResponse(List.of(Material.MaterialStatus.APPROVED))
        materialStatusService.showStatusesToChange(1L)
                >> response

        when:
        def result = materialController.showStatusesToChange(1L)

        then:
        Assert.that(result.body == response)
    }

    def "shouldManage"() {
        given:
        def response = new MaterialPageable(
                1L, "title", "desc", "link", Material.MaterialStatus.APPROVED,
                new MaterialPageable.TypeDetails(1L, "name"),
                new ArrayList<MaterialPageable.TagDetails>(),
                3.0D, new MaterialPageable.AuthorDetails(1L, "name"),
                LocalDateTime.now()
        )
        materialManageService.manage(
                _ as MaterialControllerDefinition.MaterialFilterSearchParams,
                _ as PageRequest
        ) >> new PageImpl<MaterialPageable>(List.of(response))

        when:
        def result = materialController.manage(
                1, 1, MaterialControllerDefinition.MaterialSortField.ID,
                Sort.Direction.ASC,
                new MaterialControllerDefinition.MaterialFilterSearchParams()
        )

        then:
        Assert.that(result.body.content == List.of(response))
    }
}
