package com.gestaosaas.gestaosaas;

import com.gestaosaas.gestaosaas.entity.*;
import com.gestaosaas.gestaosaas.repository.ClienteRepository;
import com.gestaosaas.gestaosaas.repository.ProdutoRepository;
import com.gestaosaas.gestaosaas.repository.VendaRepository;
import com.gestaosaas.gestaosaas.service.AuthService;
import com.gestaosaas.gestaosaas.service.VendaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private VendaService service;

    @Test
    void deveSalvarVendaDaEmpresaDoUsuarioLogado() {
        Empresa empresa = new Empresa();
        empresa.setNome("Empresa A");

        Usuario usuario = new Usuario();
        usuario.setEmpresa(empresa);

        Cliente cliente = new Cliente();
        cliente.setNome("João");
        cliente.setEmpresa(empresa);

        Produto produto = new Produto();
        produto.setNome("Teclado");
        produto.setPreco(new BigDecimal("100.00"));
        produto.setQuantidadeEstoque(10);
        produto.setEmpresa(empresa);

        when(authService.usuarioLogado()).thenReturn(usuario);
        when(clienteRepository.findByIdAndEmpresa(1L, empresa)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findByIdAndEmpresa(1L, empresa)).thenReturn(Optional.of(produto));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venda venda = service.salvar(1L, 1L, 2);

        assertNotNull(venda);
        assertEquals(empresa, venda.getEmpresa());
        assertEquals(cliente, venda.getCliente());
        assertEquals(new BigDecimal("200.00"), venda.getValorTotal());
        assertEquals(8, produto.getQuantidadeEstoque());
        verify(produtoRepository, times(1)).save(produto);
        verify(vendaRepository, times(1)).save(any(Venda.class));
    }

    @Test
    void deveLancarErroQuandoProdutoNaoPertenceAEmpresa() {
        Empresa empresa = new Empresa();
        empresa.setNome("Empresa A");

        Usuario usuario = new Usuario();
        usuario.setEmpresa(empresa);

        Cliente cliente = new Cliente();
        cliente.setEmpresa(empresa);

        when(authService.usuarioLogado()).thenReturn(usuario);
        when(clienteRepository.findByIdAndEmpresa(1L, empresa)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findByIdAndEmpresa(1L, empresa)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.salvar(1L, 1L, 1));

        assertEquals("Produto não encontrado", ex.getMessage());
    }
}