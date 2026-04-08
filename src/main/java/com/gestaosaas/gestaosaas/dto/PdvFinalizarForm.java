package com.gestaosaas.gestaosaas.dto;

import com.gestaosaas.gestaosaas.entity.FormaPagamento;

import java.math.BigDecimal;

public class PdvFinalizarForm {

    private Long clienteId;
    private FormaPagamento formaPagamento;
    private BigDecimal valorRecebido;
    private String itensJson;

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
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

    public String getItensJson() {
        return itensJson;
    }

    public void setItensJson(String itensJson) {
        this.itensJson = itensJson;
    }
}