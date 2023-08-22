package pl.sknikod.kodemy.infrastructure.technology.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import pl.sknikod.kodemy.MvcIntegrationSpec
import pl.sknikod.kodemy.WithKodemyMockUser
import pl.sknikod.kodemy.infrastructure.technology.TechnologyService

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TechnologyController)
class TechnologyControllerSpec extends MvcIntegrationSpec {

    @Autowired
    TechnologyService technologyService

    def objectMapper = new ObjectMapper()

    def "should thrown Unauthorized when creating technology without authentication"() {
        when:
        def result = mockMvc.perform(post("/api/technologies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TechnologyAddRequest())))

        then:
        result.andExpect(status().isUnauthorized())
    }

    @WithKodemyMockUser
    def "should create technology"() {
        given:
        def request = new TechnologyAddRequest()
        request.setName("Nazwa")
        def response = new TechnologyAddResponse()
        response.setId(1L)
        response.setName(request.getName())

        technologyService.addTechnology(_ as TechnologyAddRequest) >> response

        when:
        def result = mockMvc.perform(post("/api/technologies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(response)))
    }
}
