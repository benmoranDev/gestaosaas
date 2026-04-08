package com.gestaosaas.gestaosaas.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private LocalDate dataVenda;

    @Column(precision = 12, scale = 2)
    private BigDecimal valorTotal;

    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", length = 30)
    private FormaPagamento formaPagamento;

    @Column(name = "valor_recebido", precision = 12, scale = 2)
    private BigDecimal valorRecebido;

    @Column(name = "troco", precision = 12, scale = 2)
    private BigDecimal troco;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itens = new ArrayList<>();

    public void calcularTotal() {
        this.valorTotal = itens.stream()
                .map(item -> item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Venda buscarPorId(Long id) {
        throw new UnsupportedOperationException("Implemente usando seu VendaRepository real");
    }

    public Venda salvar(Venda venda) {
        throw new UnsupportedOperationException("Implemente usando seu VendaRepository real");
    }

    // Marca a venda como aguardando pagamento Pix.
    public Venda marcarComoAguardandoPix(Venda venda) {
        venda.setStatusPagamento(StatusPagamento.AGUARDANDO_PIX);
        return salvar(venda);
    }

    // Confirma pagamento da venda.
    public Venda confirmarPagamentoPix(Venda venda) {
        venda.setStatusPagamento(StatusPagamento.PAGO);
        return salvar(venda);
    }

    // Na entidade Venda
    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    // getter e setter
    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento = StatusPagamento.PENDENTE;

    @Enumerated(EnumType.STRING)
    private TipoDocumentoFiscal tipoDocumentoFiscal = TipoDocumentoFiscal.NENHUM;

    private String numeroDocumentoFiscal;
    private String serieDocumentoFiscal;
    private String chaveAcessoFiscal;
    private String protocoloAutorizacaoFiscal;
    private LocalDateTime dataEmissaoFiscal;



    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamento statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public TipoDocumentoFiscal getTipoDocumentoFiscal() {
        return tipoDocumentoFiscal;
    }

    public void setTipoDocumentoFiscal(TipoDocumentoFiscal tipoDocumentoFiscal) {
        this.tipoDocumentoFiscal = tipoDocumentoFiscal;
    }

    public String getNumeroDocumentoFiscal() {
        return numeroDocumentoFiscal;
    }

    public void setNumeroDocumentoFiscal(String numeroDocumentoFiscal) {
        this.numeroDocumentoFiscal = numeroDocumentoFiscal;
    }

    public String getSerieDocumentoFiscal() {
        return serieDocumentoFiscal;
    }

    public void setSerieDocumentoFiscal(String serieDocumentoFiscal) {
        this.serieDocumentoFiscal = serieDocumentoFiscal;
    }

    public String getChaveAcessoFiscal() {
        return chaveAcessoFiscal;
    }

    public void setChaveAcessoFiscal(String chaveAcessoFiscal) {
        this.chaveAcessoFiscal = chaveAcessoFiscal;
    }

    public String getProtocoloAutorizacaoFiscal() {
        return protocoloAutorizacaoFiscal;
    }

    public void setProtocoloAutorizacaoFiscal(String protocoloAutorizacaoFiscal) {
        this.protocoloAutorizacaoFiscal = protocoloAutorizacaoFiscal;
    }

    public LocalDateTime getDataEmissaoFiscal() {
        return dataEmissaoFiscal;
    }

    public void setDataEmissaoFiscal(LocalDateTime dataEmissaoFiscal) {
        this.dataEmissaoFiscal = dataEmissaoFiscal;
    }

    public void adicionarItem(ItemVenda item) {
        item.setVenda(this);
        this.itens.add(item);
    }

    public Long getId() {
        return id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDate getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDate dataVenda) {
        this.dataVenda = dataVenda;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public BigDecimal getValorRecebido() {
        return valorRecebido;
    }

    public void setValorRecebido(BigDecimal valorRecebido) {
        this.valorRecebido = valorRecebido;
    }

    public BigDecimal getTroco() {
        return troco;
    }

    public void setTroco(BigDecimal troco) {
        this.troco = troco;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }
}