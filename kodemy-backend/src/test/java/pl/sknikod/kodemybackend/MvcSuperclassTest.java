package pl.sknikod.kodemybackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import pl.sknikod.kodemybackend.configuration.SecurityConfiguration;
import pl.sknikod.kodemycommons.security.configuration.JwtConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@Import({SecurityConfiguration.class, JwtConfiguration.class})
public class MvcSuperclassTest extends SuperclassTest {
    @Autowired
    protected MockMvc mockMvc;
}
