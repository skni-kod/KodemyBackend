package pl.sknikod.kodemy.infrastructure.rest

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import pl.sknikod.kodemy.MvcIntegrationSpec
import pl.sknikod.kodemy.WithKodemyMockUser
import pl.sknikod.kodemy.infrastructure.user.UserService
import pl.sknikod.kodemy.infrastructure.user.rest.UserController

@WebMvcTest(UserController)
class UserControllerSpec extends MvcIntegrationSpec {

    @Autowired
    UserService userService

    def objectMapper = new ObjectMapper()

    @WithKodemyMockUser
    def "GetUserInfo"() {
    }

}
