package com.gestaosaas.gestaosaas;

import com.gestaosaas.gestaosaas.entity.Empresa;
import com.gestaosaas.gestaosaas.entity.Usuario;
import com.gestaosaas.gestaosaas.repository.ClienteRepository;
import com.gestaosaas.gestaosaas.repository.ProdutoRepository;
import com.gestaosaas.gestaosaas.repository.VendaRepository;
import com.gestaosaas.gestaosaas.service.AuthService;
import com.gestaosaas.gestaosaas.service.DashboardService;
import com.gestaosaas.gestaosaas.service.FinanceiroService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Map;


@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private FinanceiroService financeiroService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void deveRetornarIndicadoresDaEmpresaDoUsuarioLogado() {
        Empresa empresa = new Empresa();
        empresa.setNome("Empresa A");

        Usuario usuario = new Usuario();
        usuario.setEmpresa(empresa);

        when(authService.usuarioLogado()).thenReturn(usuario);
        when(clienteRepository.countByEmpresa(empresa)).thenReturn(12L);
        when(produtoRepository.countByEmpresa(empresa)).thenReturn(8L);
        when(vendaRepository.countByEmpresa(empresa)).thenReturn(5L);
        when(financeiroService.totalReceber()).thenReturn(new BigDecimal("1500.00"));
        when(financeiroService.totalPagar()).thenReturn(new BigDecimal("400.00"));
        when(financeiroService.saldoPrevisto()).thenReturn(new BigDecimal("1100.00"));

        Map<String, Object> indicadores = dashboardService.obterIndicadores();

        assertEquals(12L, indicadores.get("totalClientes"));
        assertEquals(8L, indicadores.get("totalProdutos"));
        assertEquals(5L, indicadores.get("totalVendas"));
        assertEquals(new BigDecimal("1500.00"), indicadores.get("totalReceber"));
        assertEquals(new BigDecimal("400.00"), indicadores.get("totalPagar"));
        assertEquals(new BigDecimal("1100.00"), indicadores.get("saldoPrevisto"));
    }
}
