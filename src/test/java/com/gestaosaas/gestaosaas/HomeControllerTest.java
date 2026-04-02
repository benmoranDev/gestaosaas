package com.gestaosaas.gestaosaas;

import com.gestaosaas.gestaosaas.controller.HomeController;
import com.gestaosaas.gestaosaas.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @Test
    void deveRenderizarDashboard() throws Exception {
        when(dashboardService.obterIndicadores()).thenReturn(Map.of(
                "totalClientes", 10L,
                "totalProdutos", 20L,
                "totalVendas", 5L,
                "totalReceber", new BigDecimal("1000.00"),
                "totalPagar", new BigDecimal("400.00"),
                "saldoPrevisto", new BigDecimal("600.00")
        ));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("totalClientes"))
                .andExpect(model().attributeExists("totalProdutos"))
                .andExpect(model().attributeExists("totalVendas"))
                .andExpect(model().attributeExists("totalReceber"))
                .andExpect(model().attributeExists("totalPagar"))
                .andExpect(model().attributeExists("saldoPrevisto"));
    }
}