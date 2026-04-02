package com.gestaosaas.gestaosaas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc
class LogoutSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveFazerLogoutComSucesso() throws Exception {
        mockMvc.perform(logout("/logout"))
                .andExpect(redirectedUrl("/login?logout=true"));
    }
}
