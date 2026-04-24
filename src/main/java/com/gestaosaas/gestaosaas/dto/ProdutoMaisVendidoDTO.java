package com.gestaosaas.gestaosaas.dto.relatorio;

import java.math.BigDecimal;

public record ProdutoMaisVendidoDTO(
    String produto,
    Long quantidadeVendida,
    BigDecimal totalVendido
) {
}
