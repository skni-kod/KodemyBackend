package pl.sknikod.kodemyauth.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class MvcTest extends BaseTest {
    @Autowired
    protected MockMvc mockMvc;

    protected final ResultActions mvcPerform(MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    protected final ResultActions mvcPerformWithBearerAuth(MockHttpServletRequestBuilder request) throws Exception {
        return this.mvcPerform(request.header(HttpHeaders.AUTHORIZATION, "Bearer "));
    }
}
