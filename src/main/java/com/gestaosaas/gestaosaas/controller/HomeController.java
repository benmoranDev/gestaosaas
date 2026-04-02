package com.gestaosaas.gestaosaas.controller;

import com.gestaosaas.gestaosaas.service.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;


@Controller
public class HomeController {

    private final DashboardService dashboardService;

    public HomeController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','OPERADOR')")
    @GetMapping("/")
    public String home(Model model) {
        Map<String, Object> indicadores = dashboardService.obterIndicadores();

        model.addAttribute("totalClientes", indicadores.get("totalClientes"));
        model.addAttribute("totalProdutos", indicadores.get("totalProdutos"));
        model.addAttribute("totalVendas", indicadores.get("totalVendas"));
        model.addAttribute("totalReceber", indicadores.get("totalReceber"));
        model.addAttribute("totalPagar", indicadores.get("totalPagar"));
        model.addAttribute("saldoPrevisto", indicadores.get("saldoPrevisto"));

        model.addAttribute("totalReceberFormatado", indicadores.get("totalReceberFormatado"));
        model.addAttribute("totalPagarFormatado", indicadores.get("totalPagarFormatado"));
        model.addAttribute("saldoPrevistoFormatado", indicadores.get("saldoPrevistoFormatado"));

        return "index";
    }
}