package com.gestaosaas.gestaosaas;

import com.gestaosaas.gestaosaas.entity.Produto;
import com.gestaosaas.gestaosaas.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;


import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository repository;

    @Test
    void deveSalvarProduto() {
        Produto produto = new Produto();
        produto.setNome("Teclado");
        produto.setDescricao("Teclado USB");
        produto.setPreco(new BigDecimal("99.90"));
        produto.setQuantidadeEstoque(15);

        Produto salvo = repository.save(produto);

        assertNotNull(salvo.getId());
        assertEquals("Teclado", salvo.getNome());
    }

    @Test
    void deveBuscarProdutoPorId() {
        Produto produto = new Produto();
        produto.setNome("Mouse");
        produto.setDescricao("Mouse óptico");
        produto.setPreco(new BigDecimal("49.90"));
        produto.setQuantidadeEstoque(20);

        Produto salvo = repository.save(produto);

        Optional<Produto> encontrado = repository.findById(salvo.getId());

        assertTrue(encontrado.isPresent());
        assertEquals("Mouse", encontrado.get().getNome());
    }
}