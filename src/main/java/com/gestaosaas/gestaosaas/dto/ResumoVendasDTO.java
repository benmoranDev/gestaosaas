package com.gestaosaas.gestaosaas.dto.relatorio;

import java.math.BigDecimal;
import java.math.RoundingMode;


public record ResumoVendasDTO(
    BigDecimal totalVendido,
    Long quantidadeVendas,
    BigDecimal ticketMedio
) {
    public ResumoVendasDTO(BigDecimal totalVendido, Long quantidadeVendas) {
        this(
            totalVendido != null ? totalVendido : BigDecimal.ZERO,
            quantidadeVendas != null ? quantidadeVendas : 0L,
            calcularTicketMedio(totalVendido, quantidadeVendas)
        );
    }

    private static BigDecimal calcularTicketMedio(BigDecimal total, Long quantidade) {
        BigDecimal totalSeguro = total != null ? total : BigDecimal.ZERO;
        long qtdSegura = quantidade != null ? quantidade : 0L;

        if (qtdSegura == 0L) {
            return BigDecimal.ZERO;
        }

        return totalSeguro.divide(BigDecimal.valueOf(qtdSegura), 2, RoundingMode.HALF_UP);
    }
}
