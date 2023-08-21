package pl.sknikod.kodemy.infrastructure.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import pl.sknikod.kodemy.MvcIntegrationSpec
import pl.sknikod.kodemy.infrastructure.model.entity.MaterialStatus
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialOpenSearch

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(CategoryMaterialController)
class CategoryMaterialControllerSpec extends MvcIntegrationSpec {

    @Autowired
    CategoryService categoryService

    def "should return material basing on category"() {
        given:
        def materialOpenSearch = new MaterialOpenSearch(
                1L,
                "Tytul",
                "Opis",
                "http://localhost:8181",
                MaterialStatus.APPROVED,
                true,
                "user",
                new Date(),
                1L
        )

        categoryService.showMaterials(_ as Long, _ as Integer) >> List.of(materialOpenSearch)

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get(
                "/api/categories/${materialOpenSearch.getId()}/materials"
        ))

        then:
        result.andExpect(status().isOk())
    }
}
