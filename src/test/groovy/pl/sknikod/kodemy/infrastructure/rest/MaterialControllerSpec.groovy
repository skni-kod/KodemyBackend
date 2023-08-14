package pl.sknikod.kodemy.infrastructure.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import pl.sknikod.kodemy.MvcIntegrationSpec
import pl.sknikod.kodemy.WithKodemyMockUser
import pl.sknikod.kodemy.infrastructure.model.entity.MaterialStatus
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialCreateRequest
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialCreateResponse

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(MaterialController)
class MaterialControllerSpec extends MvcIntegrationSpec {

    @Autowired
    MaterialService materialService

    def objectMapper = new ObjectMapper()

    final def materialCreateRequest = new MaterialCreateRequest(
            "Tytul",
            "Opis",
            "http://localhost:8181",
            1L,
            1L,
            Collections.emptySet()
    )

    def "should throw Unauthorized when create material without authentication"() {
        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.post("/api/materials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(materialCreateRequest))
        )

        then:
        result.andExpect(status().isUnauthorized())
    }

    @WithKodemyMockUser
    def "should create material"() {
        def materialCreateResponse = new MaterialCreateResponse(1L, materialCreateRequest.getTitle(), MaterialStatus.PENDING)
        materialService.create(_ as MaterialCreateRequest) >> materialCreateResponse

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.post("/api/materials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(materialCreateRequest))
        )

        then:
        result.andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(materialCreateResponse)))
    }
}
