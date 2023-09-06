package pl.sknikod.kodemy.infrastructure.material

import pl.sknikod.kodemy.MvcIntegrationSpec
import pl.sknikod.kodemy.infrastructure.common.entity.MaterialStatus
import pl.sknikod.kodemy.infrastructure.material.rest.MaterialCreateRequest
import pl.sknikod.kodemy.infrastructure.material.rest.MaterialCreateResponse

class MaterialServiceSpec extends MvcIntegrationSpec {
//
//    def materialCreateUseCase = Mock(MaterialCreateUseCase)
//
//    def materialService = new MaterialService(
//            null,
//            null,
//            null,
//            null,
//            materialCreateUseCase
//    )

//    def "should create material"() {
//        given:
//        def request = new MaterialCreateRequest(
//                "Tytul",
//                "Opis",
//                "http://localhost:8181",
//                1L,
//                1L,
//                Collections.emptySet()
//        )
//
//        materialCreateUseCase.execute(_ as MaterialCreateRequest) >> new MaterialCreateResponse(
//                1L, "Title", MaterialStatus.PENDING
//        )
//
//        when:
//        materialService.create(request)
//
//        then:
//        1 * materialCreateUseCase.execute(_ as MaterialCreateRequest)
//    }
}
