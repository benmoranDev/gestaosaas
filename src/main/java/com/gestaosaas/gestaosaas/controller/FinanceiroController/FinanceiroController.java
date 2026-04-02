package com.gestaosaas.gestaosaas.controller.FinanceiroController;

import com.gestaosaas.gestaosaas.entity.ContaPagar;
import com.gestaosaas.gestaosaas.entity.ContaReceber;
import com.gestaosaas.gestaosaas.service.FinanceiroService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

// Controller responsável pelo módulo financeiro
@Controller
@RequestMapping("/financeiro")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
public class FinanceiroController {

    private final FinanceiroService financeiroService;

    public FinanceiroController(FinanceiroService financeiroService) {
        this.financeiroService = financeiroService;
    }

    // Exibe o dashboard financeiro
    @GetMapping
    public String dashboard(Model model) {
        BigDecimal totalReceber = financeiroService.totalReceber();
        BigDecimal totalPagar = financeiroService.totalPagar();
        BigDecimal saldoPrevisto = financeiroService.saldoPrevisto();

        NumberFormat moedaBR = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        model.addAttribute("totalReceber", totalReceber);
        model.addAttribute("totalPagar", totalPagar);
        model.addAttribute("saldoPrevisto", saldoPrevisto);

        model.addAttribute("totalReceberFormatado", moedaBR.format(totalReceber));
        model.addAttribute("totalPagarFormatado", moedaBR.format(totalPagar));
        model.addAttribute("saldoPrevistoFormatado", moedaBR.format(saldoPrevisto));

        model.addAttribute("contasReceber", financeiroService.listarContasReceber());
        model.addAttribute("contasPagar", financeiroService.listarContasPagar());

        return "financeiro/index";
    }

    // Exibe formulário de nova conta a receber
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERADOR')")
    @GetMapping("/receber/nova")
    public String novaContaReceber(Model model) {
        model.addAttribute("contaReceber", new ContaReceber());
        return "financeiro/form-receber";
    }

    // Salva uma nova conta a receber
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERADOR')")
    @PostMapping("/receber/salvar")
    public String salvarContaReceber(@Valid @ModelAttribute ContaReceber contaReceber,
                                     BindingResult result) {
        if (result.hasErrors()) {
            return "financeiro/form-receber";
        }

        financeiroService.salvarContaReceber(contaReceber);
        return "redirect:/financeiro";
    }

    // Exibe formulário de nova conta a pagar
    @GetMapping("/pagar/nova")
    public String novaContaPagar(Model model) {
        model.addAttribute("contaPagar", new ContaPagar());
        return "financeiro/form-pagar";
    }

    // Salva uma nova conta a pagar
    @PostMapping("/pagar/salvar")
    public String salvarContaPagar(@Valid @ModelAttribute ContaPagar contaPagar,
                                   BindingResult result) {
        if (result.hasErrors()) {
            return "financeiro/form-pagar";
        }

        financeiroService.salvarContaPagar(contaPagar);
        return "redirect:/financeiro";
    }
}