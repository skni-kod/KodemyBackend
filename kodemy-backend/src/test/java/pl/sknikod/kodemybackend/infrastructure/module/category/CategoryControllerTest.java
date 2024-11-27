package pl.sknikod.kodemybackend.infrastructure.module.category;

import org.junit.jupiter.api.Test;
import pl.sknikod.kodemybackend.MvcSuperclassTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerTest extends MvcSuperclassTest {
    @Test
    void getCategoryDetails_success() throws Exception {
        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}