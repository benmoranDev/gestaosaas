package com.gestaosaas.gestaosaas.dto.relatorio;

import com.gestaosaas.gestaosaas.entity.FormaPagamento;

import java.math.BigDecimal;

public record FormaPagamentoDTO(
    FormaPagamento formaPagamento,
    Long quantidade,
    BigDecimal total
) {
}
