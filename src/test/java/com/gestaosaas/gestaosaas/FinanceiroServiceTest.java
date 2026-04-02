package com.gestaosaas.gestaosaas;

import com.gestaosaas.gestaosaas.entity.ContaPagar;
import com.gestaosaas.gestaosaas.entity.ContaReceber;
import com.gestaosaas.gestaosaas.entity.Empresa;
import com.gestaosaas.gestaosaas.entity.Usuario;
import com.gestaosaas.gestaosaas.repository.ContaPagarRepository;
import com.gestaosaas.gestaosaas.repository.ContaReceberRepository;
import com.gestaosaas.gestaosaas.service.AuthService;
import com.gestaosaas.gestaosaas.service.FinanceiroService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinanceiroServiceTest {

    @Mock
    private ContaReceberRepository contaReceberRepository;

    @Mock
    private ContaPagarRepository contaPagarRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private FinanceiroService financeiroService;

    @Test
    void deveCalcularTotaisFinanceirosDaEmpresaDoUsuarioLogado() {
        Empresa empresa = new Empresa();
        empresa.setNome("Empresa A");

        Usuario usuario = new Usuario();
        usuario.setEmpresa(empresa);

        ContaReceber r1 = new ContaReceber();
        r1.setValor(new BigDecimal("100.00"));
        r1.setRecebida(false);
        r1.setEmpresa(empresa);

        ContaReceber r2 = new ContaReceber();
        r2.setValor(new BigDecimal("50.00"));
        r2.setRecebida(true);
        r2.setEmpresa(empresa);

        ContaPagar p1 = new ContaPagar();
        p1.setValor(new BigDecimal("30.00"));
        p1.setPaga(false);
        p1.setEmpresa(empresa);

        ContaPagar p2 = new ContaPagar();
        p2.setValor(new BigDecimal("20.00"));
        p2.setPaga(true);
        p2.setEmpresa(empresa);

        when(authService.usuarioLogado()).thenReturn(usuario);
        when(contaReceberRepository.findByEmpresa(empresa)).thenReturn(List.of(r1, r2));
        when(contaPagarRepository.findByEmpresa(empresa)).thenReturn(List.of(p1, p2));

        assertEquals(new BigDecimal("100.00"), financeiroService.totalReceber());
        assertEquals(new BigDecimal("30.00"), financeiroService.totalPagar());
        assertEquals(new BigDecimal("70.00"), financeiroService.saldoPrevisto());
    }
}