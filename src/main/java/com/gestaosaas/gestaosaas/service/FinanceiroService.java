package com.gestaosaas.gestaosaas.service;

import com.gestaosaas.gestaosaas.entity.ContaPagar;
import com.gestaosaas.gestaosaas.entity.ContaReceber;
import com.gestaosaas.gestaosaas.entity.Empresa;
import com.gestaosaas.gestaosaas.repository.ContaPagarRepository;
import com.gestaosaas.gestaosaas.repository.ContaReceberRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class FinanceiroService {

    private final ContaReceberRepository contaReceberRepository;
    private final ContaPagarRepository contaPagarRepository;
    private final AuthService authService;

    public FinanceiroService(ContaReceberRepository contaReceberRepository,
                             ContaPagarRepository contaPagarRepository,
                             AuthService authService) {
        this.contaReceberRepository = contaReceberRepository;
        this.contaPagarRepository = contaPagarRepository;
        this.authService = authService;
    }

    public List<ContaReceber> listarContasReceber() {
        Empresa empresa = authService.usuarioLogado().getEmpresa();
        return contaReceberRepository.findByEmpresa(empresa);
    }

    public List<ContaPagar> listarContasPagar() {
        Empresa empresa = authService.usuarioLogado().getEmpresa();
        return contaPagarRepository.findByEmpresa(empresa);
    }

    public ContaReceber salvarContaReceber(ContaReceber conta) {
        Empresa empresa = authService.usuarioLogado().getEmpresa();
        conta.setEmpresa(empresa);
        return contaReceberRepository.save(conta);
    }

    public ContaPagar salvarContaPagar(ContaPagar conta) {
        Empresa empresa = authService.usuarioLogado().getEmpresa();
        conta.setEmpresa(empresa);
        return contaPagarRepository.save(conta);
    }

    public BigDecimal totalReceber() {
        return listarContasReceber().stream()
                .filter(c -> !c.isRecebida())
                .map(ContaReceber::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalPagar() {
        return listarContasPagar().stream()
                .filter(c -> !c.isPaga())
                .map(ContaPagar::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal saldoPrevisto() {
        return totalReceber().subtract(totalPagar());
    }
}