package pl.sknikod.kodemy.infrastructure.health.rest

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.ConfigurableApplicationContext
import pl.sknikod.kodemy.MvcIntegrationSpec
import pl.sknikod.kodemy.WithKodemyMockUser

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ActuatorController)
class ActuatorControllerSpec extends MvcIntegrationSpec {

    def "should throw Unauthorized when getting info without authentication"() {
        when:
        def result = mockMvc.perform(get("/api/actuator/info"))

        then:
        result.andExpect(status().isUnauthorized())
    }

    @WithKodemyMockUser
    def "should throw Forbidden when getting info without authority"() {
        when:
        def result = mockMvc.perform(get("/api/actuator/info"))

        then:
        result.andExpect(status().isForbidden())
    }

    @WithKodemyMockUser(authorities = "CAN_USE_ACTUATOR")
    def "should return info"() {
        when:
        def result = mockMvc.perform(get("/api/actuator/info"))

        then:
        result.andExpect(status().isOk())
    }

    def "should throw Unauthorized when refresh context without authentication"() {
        when:
        def result = mockMvc.perform(post("/api/actuator/refresh"))

        then:
        result.andExpect(status().isUnauthorized())
    }

    @WithKodemyMockUser
    def "should throw Forbidden when refresh context without authority"() {
        when:
        def result = mockMvc.perform(post("/api/actuator/refresh"))

        then:
        result.andExpect(status().isForbidden())
    }


    @WithKodemyMockUser(authorities = "CAN_USE_ACTUATOR")
    def "should refresh context"() {
        given:
        def context = Mock(ConfigurableApplicationContext)
        def actuatorController = new ActuatorController(context, [])

        when:
        actuatorController.refresh()

        then:
        1 * context.refresh()
    }
}
