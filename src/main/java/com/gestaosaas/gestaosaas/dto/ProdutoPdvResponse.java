package com.gestaosaas.gestaosaas.dto;

import java.math.BigDecimal;

import java.math.BigDecimal;

public record ProdutoPdvResponse(
        Long id,
        String nome,
        BigDecimal preco,
        Integer quantidadeEstoque,
        String descricao
) {
}
