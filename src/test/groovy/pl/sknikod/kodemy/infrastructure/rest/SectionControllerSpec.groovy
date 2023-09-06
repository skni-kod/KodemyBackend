package pl.sknikod.kodemy.infrastructure.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import pl.sknikod.kodemy.MvcIntegrationSpec
import pl.sknikod.kodemy.infrastructure.common.entity.Section
import pl.sknikod.kodemy.infrastructure.section.SectionService
import pl.sknikod.kodemy.infrastructure.section.rest.SectionController
import pl.sknikod.kodemy.infrastructure.section.rest.SingleSectionResponse

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(SectionController)
class SectionControllerSpec extends MvcIntegrationSpec {

    @Autowired
    SectionService sectionService

    def objectMapper = new ObjectMapper()

    def "should get all sections"() {
        given:
        def section = new Section()
        section.setId(1L)
        section.setName("Name")
        def sectionResponse = List.of(
                new SingleSectionResponse(1L, section.getName(),
                        List.of(new SingleSectionResponse.CategoryDetails(1L, "Name"))
                )
        )
        sectionService.getAllSections() >> sectionResponse

        when:
        def result = mockMvc.perform(get("/api/sections"))

        then:
        result
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(sectionResponse)))
    }
}
