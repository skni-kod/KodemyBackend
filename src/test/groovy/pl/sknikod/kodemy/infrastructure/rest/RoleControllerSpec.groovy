package pl.sknikod.kodemy.infrastructure.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import pl.sknikod.kodemy.MvcIntegrationSpec
import pl.sknikod.kodemy.WithKodemyMockUser
import pl.sknikod.kodemy.infrastructure.common.entity.RoleName
import pl.sknikod.kodemy.infrastructure.user.RoleService
import pl.sknikod.kodemy.infrastructure.user.rest.RoleController

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(RoleController)
class RoleControllerSpec extends MvcIntegrationSpec {

    @Autowired
    RoleService roleService

    @WithKodemyMockUser
    def "GetRoles"() {
        given:
        roleService.getRoles() >> RoleName.values()

        when:
        def result = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/roles"))

        then:
        result.andExpect(status().isOk())
                .andExpect(content()
                        .json(new ObjectMapper().writeValueAsString(RoleName.values())))
    }
}
