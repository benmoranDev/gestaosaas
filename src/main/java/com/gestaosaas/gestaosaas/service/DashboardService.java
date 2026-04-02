package com.gestaosaas.gestaosaas.service;

import com.gestaosaas.gestaosaas.entity.ContaPagar;
import com.gestaosaas.gestaosaas.entity.ContaReceber;
import com.gestaosaas.gestaosaas.entity.Empresa;
import com.gestaosaas.gestaosaas.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
public class DashboardService {

    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final VendaRepository vendaRepository;
    private final ContaReceberRepository contaReceberRepository;
    private final ContaPagarRepository contaPagarRepository;

    public DashboardService(ClienteRepository clienteRepository,
                            ProdutoRepository produtoRepository,
                            VendaRepository vendaRepository,
                            ContaReceberRepository contaReceberRepository,
                            ContaPagarRepository contaPagarRepository) {
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.vendaRepository = vendaRepository;
        this.contaReceberRepository = contaReceberRepository;
        this.contaPagarRepository = contaPagarRepository;
    }

    public Map<String, Object> obterIndicadores() {
        Map<String, Object> indicadores = new HashMap<>();

        long totalClientes = clienteRepository.count();
        long totalProdutos = produtoRepository.count();
        long totalVendas = vendaRepository.count();

        BigDecimal totalReceber = contaReceberRepository.findAll()
                .stream()
                .map(ContaReceber::getValor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPagar = contaPagarRepository.findAll()
                .stream()
                .map(ContaPagar::getValor)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoPrevisto = totalReceber.subtract(totalPagar);

        NumberFormat moedaBR = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        indicadores.put("totalClientes", totalClientes);
        indicadores.put("totalProdutos", totalProdutos);
        indicadores.put("totalVendas", totalVendas);
        indicadores.put("totalReceber", totalReceber);
        indicadores.put("totalPagar", totalPagar);
        indicadores.put("saldoPrevisto", saldoPrevisto);

        indicadores.put("totalReceberFormatado", moedaBR.format(totalReceber));
        indicadores.put("totalPagarFormatado", moedaBR.format(totalPagar));
        indicadores.put("saldoPrevistoFormatado", moedaBR.format(saldoPrevisto));

        return indicadores;
    }
}