package pl.sknikod.kodemy.infrastructure.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import pl.sknikod.kodemy.MvcIntegrationSpec
import pl.sknikod.kodemy.infrastructure.rest.model.SingleSectionResponse
import pl.sknikod.kodemy.infrastructure.rest.model.SingleTypeResponse

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(SectionController)
class SectionControllerSpec extends MvcIntegrationSpec {

    @Autowired
    SectionService sectionService

    def "should return list of all sections"() {
        given:
        sectionService.getAllSections() >> List.of(
                new SingleSectionResponse(1L, "Nazwa", Collections.emptyList()),
                new SingleSectionResponse(2l, "Nazwa 2", Collections.emptyList())
        )

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/sections"))

        then:
        result.andExpect { status().isOk() }
    }
}
