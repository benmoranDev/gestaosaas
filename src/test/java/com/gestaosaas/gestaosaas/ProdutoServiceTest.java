package com.gestaosaas.gestaosaas;

import com.gestaosaas.gestaosaas.entity.Empresa;
import com.gestaosaas.gestaosaas.entity.Produto;
import com.gestaosaas.gestaosaas.entity.Usuario;
import com.gestaosaas.gestaosaas.repository.ProdutoRepository;
import com.gestaosaas.gestaosaas.service.AuthService;
import com.gestaosaas.gestaosaas.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ProdutoService service;

    @Test
    void deveListarProdutosDaEmpresaDoUsuarioLogado() {
        Empresa empresa = new Empresa();
        empresa.setNome("Empresa A");

        Usuario usuario = new Usuario();
        usuario.setEmpresa(empresa);

        Produto produto = new Produto();
        produto.setNome("Notebook");
        produto.setPreco(new BigDecimal("3500.00"));
        produto.setQuantidadeEstoque(5);
        produto.setEmpresa(empresa);

        when(authService.usuarioLogado()).thenReturn(usuario);
        when(repository.findByEmpresa(empresa)).thenReturn(List.of(produto));

        List<Produto> produtos = service.listarTodos();

        assertEquals(1, produtos.size());
        assertEquals("Notebook", produtos.get(0).getNome());
        verify(repository, times(1)).findByEmpresa(empresa);
    }

    @Test
    void deveSalvarProdutoNaEmpresaDoUsuarioLogado() {
        Empresa empresa = new Empresa();
        empresa.setNome("Empresa A");

        Usuario usuario = new Usuario();
        usuario.setEmpresa(empresa);

        Produto produto = new Produto();
        produto.setNome("Mouse");
        produto.setPreco(new BigDecimal("80.00"));
        produto.setQuantidadeEstoque(20);

        when(authService.usuarioLogado()).thenReturn(usuario);
        when(repository.save(produto)).thenReturn(produto);

        Produto salvo = service.salvar(produto);

        assertEquals(empresa, salvo.getEmpresa());
        verify(repository, times(1)).save(produto);
    }
}