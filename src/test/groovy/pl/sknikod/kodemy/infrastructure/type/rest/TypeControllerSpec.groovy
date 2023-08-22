package pl.sknikod.kodemy.infrastructure.type.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import pl.sknikod.kodemy.MvcIntegrationSpec
import pl.sknikod.kodemy.infrastructure.type.TypeService

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TypeController)
class TypeControllerSpec extends MvcIntegrationSpec {

    @Autowired
    TypeService typeService

    def "should return list of all types"() {
        given:
        typeService.getAllTypes() >> List.of(
                new SingleTypeResponse(1L, "Nazwa"),
                new SingleTypeResponse(2l, "Nazwa 2")
        )

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/types"))

        then:
        result.andExpect { status().isOk() }
    }
}
