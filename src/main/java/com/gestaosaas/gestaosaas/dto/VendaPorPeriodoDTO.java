package com.gestaosaas.gestaosaas.dto.relatorio;

import java.math.BigDecimal;

public record VendaPorPeriodoDTO(
    String periodo,
    BigDecimal totalVendido,
    Long quantidadeVendas
) {}
