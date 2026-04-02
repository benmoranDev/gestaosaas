package com.gestaosaas.gestaosaas;

import com.gestaosaas.gestaosaas.config.SecurityConfig;
import com.gestaosaas.gestaosaas.controller.ClienteController;
import com.gestaosaas.gestaosaas.entity.Cliente;
import com.gestaosaas.gestaosaas.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@Import(SecurityConfig.class)
class ClienteControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private ClienteService clienteService;


    @Test
    void deveRedirecionarParaLoginQuandoNaoAutenticado() throws Exception {
        mockMvc.perform(get("/clientes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveListarClientesQuandoAutenticado() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João");
        cliente.setEmail("joao@email.com");

        when(clienteService.listarTodos()).thenReturn(List.of(cliente));

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/lista"))
                .andExpect(model().attributeExists("clientes"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveAbrirFormularioNovoCliente() throws Exception {
        mockMvc.perform(get("/clientes/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/form"))
                .andExpect(model().attributeExists("cliente"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveSalvarClienteValido() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Maria");
        cliente.setEmail("maria@email.com");

        when(clienteService.salvar(any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(post("/clientes/salvar")
                        .with(csrf())
                        .param("nome", "Maria")
                        .param("email", "maria@email.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes"));

        verify(clienteService, times(1)).salvar(any(Cliente.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void naoDeveSalvarClienteInvalido() throws Exception {
        mockMvc.perform(post("/clientes/salvar")
                        .with(csrf())
                        .param("nome", "")
                        .param("email", "email-invalido"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/form"))
                .andExpect(model().attributeHasFieldErrors("cliente", "nome", "email"));

        verify(clienteService, never()).salvar(any(Cliente.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveAbrirFormularioEdicao() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Carlos");
        cliente.setEmail("carlos@email.com");

        when(clienteService.buscarPorId(anyLong())).thenReturn(cliente);

        mockMvc.perform(get("/clientes/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/form"))
                .andExpect(model().attributeExists("cliente"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveExcluirCliente() throws Exception {
        mockMvc.perform(get("/clientes/excluir/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes"));

        verify(clienteService, times(1)).excluir(1L);
    }
}