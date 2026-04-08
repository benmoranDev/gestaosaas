package com.gestaosaas.gestaosaas.service;


import com.gestaosaas.gestaosaas.entity.Empresa;
import com.gestaosaas.gestaosaas.entity.Produto;
import com.gestaosaas.gestaosaas.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;
    private final AuthService authService;

    public ProdutoService(ProdutoRepository repository, AuthService authService) {
        this.repository = repository;
        this.authService = authService;
    }

    //Lista todos os produtos da empresa do usuário logado.
    public List<Produto> listarTodos() {
        Empresa empresa = obterEmpresaDoUsuarioLogado();
        return repository.findByEmpresa(empresa);
    }

    //Salva um produto vinculado à empresa do usuário logado.
    @Transactional
    public Produto salvar(Produto produto) {
        Empresa empresa = obterEmpresaDoUsuarioLogado();

        validarProduto(produto);

        produto.setEmpresa(empresa);
        return repository.save(produto);
    }

    //Busca um produto pelo id, garantindo que ele pertença à empresa logada.

    public Produto buscarPorId(Long id) {
        Empresa empresa = obterEmpresaDoUsuarioLogado();
        return repository.findByIdAndEmpresa(id, empresa)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    //Exclui um produto da empresa logada.

    @Transactional
    public void excluir(Long id) {
        Produto produto = buscarPorId(id);
        repository.delete(produto);
    }

    //Retorna a quantidade de produtos da empresa logada.

    public long contarProdutosDaEmpresa() {
        Empresa empresa = obterEmpresaDoUsuarioLogado();
        return repository.countByEmpresa(empresa);
    }

    //Retorna apenas produtos com estoque disponível.

    public List<Produto> listarProdutosDisponiveisParaVenda() {
        return listarTodos().stream()
                .filter(produto -> produto.getQuantidadeEstoque() != null && produto.getQuantidadeEstoque() > 0)
                .toList();
    }


    //Valida regras básicas do produto antes de salvar.
    private void validarProduto(Produto produto) {
        if (produto == null) {
            throw new RuntimeException("Produto inválido.");
        }

        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new RuntimeException("O nome do produto é obrigatório.");
        }

        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("O preço do produto deve ser maior que zero.");
        }

        if (produto.getQuantidadeEstoque() == null || produto.getQuantidadeEstoque() < 0) {
            throw new RuntimeException("A quantidade em estoque não pode ser negativa.");
        }
    }

    //Obtém a empresa vinculada ao usuário autenticado.
    private Empresa obterEmpresaDoUsuarioLogado() {
        return authService.usuarioLogado().getEmpresa();
    }
}