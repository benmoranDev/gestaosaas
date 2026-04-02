package com.gestaosaas.gestaosaas;

import com.gestaosaas.gestaosaas.entity.Cliente;
import com.gestaosaas.gestaosaas.entity.Empresa;
import com.gestaosaas.gestaosaas.entity.Usuario;
import com.gestaosaas.gestaosaas.repository.ClienteRepository;
import com.gestaosaas.gestaosaas.service.AuthService;
import com.gestaosaas.gestaosaas.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ClienteService service;

    @Test
    void deveListarClientesDaEmpresaDoUsuarioLogado() {
        Empresa empresa = new Empresa();
        empresa.setNome("Empresa A");

        Usuario usuario = new Usuario();
        usuario.setEmpresa(empresa);

        Cliente cliente = new Cliente();
        cliente.setNome("João");
        cliente.setEmpresa(empresa);

        when(authService.usuarioLogado()).thenReturn(usuario);
        when(repository.findByEmpresa(empresa)).thenReturn(List.of(cliente));

        List<Cliente> clientes = service.listarTodos();

        assertEquals(1, clientes.size());
        assertEquals("João", clientes.get(0).getNome());
        verify(repository, times(1)).findByEmpresa(empresa);
    }

    @Test
    void deveSalvarClienteNaEmpresaDoUsuarioLogado() {
        Empresa empresa = new Empresa();
        empresa.setNome("Empresa A");

        Usuario usuario = new Usuario();
        usuario.setEmpresa(empresa);

        Cliente cliente = new Cliente();
        cliente.setNome("Maria");

        when(authService.usuarioLogado()).thenReturn(usuario);
        when(repository.save(cliente)).thenReturn(cliente);

        Cliente salvo = service.salvar(cliente);

        assertEquals(empresa, salvo.getEmpresa());
        verify(repository, times(1)).save(cliente);
    }
}
