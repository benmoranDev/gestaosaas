package com.gestaosaas.gestaosaas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoginSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveAbrirPaginaLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void deveFazerLoginComSucesso() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("123456"))
                .andExpect(authenticated().withUsername("admin"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void deveFalharLoginComSenhaInvalida() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("errada"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error=true"));
    }
}