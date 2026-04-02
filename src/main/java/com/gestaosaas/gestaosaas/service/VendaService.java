package com.gestaosaas.gestaosaas.service;


import com.gestaosaas.gestaosaas.entity.*;
import com.gestaosaas.gestaosaas.repository.ClienteRepository;
import com.gestaosaas.gestaosaas.repository.ProdutoRepository;
import com.gestaosaas.gestaosaas.repository.VendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;



@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final AuthService authService;

    public VendaService(VendaRepository vendaRepository,
                        ClienteRepository clienteRepository,
                        ProdutoRepository produtoRepository,
                        AuthService authService) {
        this.vendaRepository = vendaRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.authService = authService;
    }

    /**
     * Lista todas as vendas da empresa do usuário logado.
     */
    public List<Venda> listarTodas() {
        Empresa empresa = obterEmpresaDoUsuarioLogado();
        return vendaRepository.findByEmpresa(empresa);
    }

    /**
     * Busca uma venda pelo id, garantindo que ela pertença à empresa logada.
     */
    public Venda buscarPorId(Long id) {
        Empresa empresa = obterEmpresaDoUsuarioLogado();
        return vendaRepository.findByIdAndEmpresa(id, empresa)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));
    }

    /**
     * Lista os clientes da empresa logada para uso em telas e formulários.
     */
    public List<Cliente> listarClientesDaEmpresa() {
        Empresa empresa = obterEmpresaDoUsuarioLogado();
        return clienteRepository.findByEmpresa(empresa);
    }

    /**
     * Lista os produtos da empresa logada para uso em telas e formulários.
     */
    public List<Produto> listarProdutosDaEmpresa() {
        Empresa empresa = obterEmpresaDoUsuarioLogado();
        return produtoRepository.findByEmpresa(empresa);
    }

    /**
     * Salva uma venda completa, recalculando itens, pagamento e estoque.
     */
    @Transactional
    public Venda salvarVendaCompleta(Venda vendaForm) {
        Empresa empresa = obterEmpresaDoUsuarioLogado();

        Venda venda = prepararVendaBase(vendaForm, empresa);
        Cliente cliente = buscarClienteSeInformado(vendaForm, empresa);

        venda.setCliente(cliente);
        venda.setObservacoes(vendaForm.getObservacoes());
        venda.setDataVenda(vendaForm.getDataVenda() != null ? vendaForm.getDataVenda() : LocalDate.now());

        BigDecimal total = processarItensDaVenda(venda, vendaForm, empresa);
        venda.setValorTotal(total);

        aplicarDadosPagamento(venda, vendaForm);

        return vendaRepository.save(venda);
    }

    /**
     * Exclui uma venda e devolve os itens ao estoque.
     */
    @Transactional
    public void excluir(Long id) {
        Venda venda = buscarPorId(id);
        devolverEstoqueItensAntigos(venda);
        vendaRepository.delete(venda);
    }

    private Venda prepararVendaBase(Venda vendaForm, Empresa empresa) {
        Venda venda;

        if (vendaForm.getId() != null) {
            venda = buscarPorId(vendaForm.getId());
            devolverEstoqueItensAntigos(venda);
            venda.getItens().clear();
        } else {
            venda = new Venda();
            venda.setEmpresa(empresa);
        }

        return venda;
    }

    private Cliente buscarClienteSeInformado(Venda vendaForm, Empresa empresa) {
        if (vendaForm.getCliente() == null || vendaForm.getCliente().getId() == null) {
            return null;
        }

        return clienteRepository.findByIdAndEmpresa(vendaForm.getCliente().getId(), empresa)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    private BigDecimal processarItensDaVenda(Venda venda, Venda vendaForm, Empresa empresa) {
        BigDecimal total = BigDecimal.ZERO;
        int itensValidos = 0;

        if (vendaForm.getItens() == null || vendaForm.getItens().isEmpty()) {
            throw new RuntimeException("Adicione pelo menos um item à venda.");
        }

        for (ItemVenda itemForm : vendaForm.getItens()) {
            if (!itemValido(itemForm)) {
                continue;
            }

            Produto produto = produtoRepository
                    .findByIdAndEmpresa(itemForm.getProduto().getId(), empresa)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            validarEstoque(produto, itemForm.getQuantidade());

            BigDecimal precoUnitario = produto.getPreco().setScale(2, RoundingMode.HALF_UP);
            BigDecimal subtotal = precoUnitario.multiply(BigDecimal.valueOf(itemForm.getQuantidade()))
                    .setScale(2, RoundingMode.HALF_UP);

            ItemVenda item = new ItemVenda();
            item.setProduto(produto);
            item.setQuantidade(itemForm.getQuantidade());
            item.setPrecoUnitario(precoUnitario);
            item.setSubtotal(subtotal);

            venda.adicionarItem(item);

            baixarEstoque(produto, itemForm.getQuantidade());

            total = total.add(subtotal);
            itensValidos++;
        }

        if (itensValidos == 0) {
            throw new RuntimeException("Adicione pelo menos um item válido à venda.");
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Aplica e valida os dados de pagamento da venda.
     */
    private void aplicarDadosPagamento(Venda venda, Venda vendaForm) {
        FormaPagamento formaPagamento = vendaForm.getFormaPagamento();

        if (formaPagamento == null) {
            throw new RuntimeException("Informe a forma de pagamento.");
        }

        venda.setFormaPagamento(formaPagamento);

        if (formaPagamento == FormaPagamento.DINHEIRO) {
            BigDecimal valorRecebido = vendaForm.getValorRecebido();

            if (valorRecebido == null) {
                throw new RuntimeException("Informe o valor recebido.");
            }

            valorRecebido = valorRecebido.setScale(2, RoundingMode.HALF_UP);

            if (valorRecebido.compareTo(venda.getValorTotal()) < 0) {
                throw new RuntimeException("O valor recebido não pode ser menor que o total da venda.");
            }

            BigDecimal troco = valorRecebido.subtract(venda.getValorTotal()).setScale(2, RoundingMode.HALF_UP);

            venda.setValorRecebido(valorRecebido);
            venda.setTroco(troco);
        } else {
            venda.setValorRecebido(null);
            venda.setTroco(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        }
    }

    private boolean itemValido(ItemVenda itemForm) {
        return itemForm != null
                && itemForm.getProduto() != null
                && itemForm.getProduto().getId() != null
                && itemForm.getQuantidade() != null
                && itemForm.getQuantidade() > 0;
    }

    private void validarEstoque(Produto produto, Integer quantidadeSolicitada) {
        if (produto.getQuantidadeEstoque() == null || produto.getQuantidadeEstoque() < quantidadeSolicitada) {
            throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
        }
    }

    private void baixarEstoque(Produto produto, Integer quantidadeVendida) {
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidadeVendida);
        produtoRepository.save(produto);
    }

    private void devolverEstoqueItensAntigos(Venda venda) {
        if (venda.getItens() == null || venda.getItens().isEmpty()) {
            return;
        }

        for (ItemVenda item : venda.getItens()) {
            if (item.getProduto() == null || item.getQuantidade() == null) {
                continue;
            }

            Produto produto = item.getProduto();
            Integer estoqueAtual = produto.getQuantidadeEstoque() != null ? produto.getQuantidadeEstoque() : 0;
            produto.setQuantidadeEstoque(estoqueAtual + item.getQuantidade());
            produtoRepository.save(produto);
        }
    }

    private Empresa obterEmpresaDoUsuarioLogado() {
        return authService.usuarioLogado().getEmpresa();
    }
}