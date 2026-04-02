package com.gestaosaas.gestaosaas.controller.ProdutoController;

import com.gestaosaas.gestaosaas.entity.ItemVenda;
import com.gestaosaas.gestaosaas.entity.Venda;
import com.gestaosaas.gestaosaas.repository.ClienteRepository;
import com.gestaosaas.gestaosaas.repository.ProdutoRepository;
import com.gestaosaas.gestaosaas.service.ClienteService;
import com.gestaosaas.gestaosaas.service.ProdutoService;
import com.gestaosaas.gestaosaas.service.VendaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/vendas")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERADOR')")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    /**
     * Lista as vendas da empresa do usuário logado.
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("vendas", vendaService.listarTodas());
        return "vendas/lista";
    }

    /**
     * Exibe o formulário de nova venda.
     */
    @GetMapping("/novo")
    public String novaVenda(Model model) {
        Venda venda = new Venda();
        venda.setDataVenda(LocalDate.now());

        ItemVenda item = new ItemVenda();
        item.setQuantidade(1);
        item.setPrecoUnitario(BigDecimal.ZERO);
        item.setSubtotal(BigDecimal.ZERO);

        venda.adicionarItem(item);

        prepararFormulario(model, venda);
        return "vendas/form";
    }

    /**
     * Exibe o formulário de edição de venda.
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Venda venda = vendaService.buscarPorId(id);

        if (venda.getItens() == null || venda.getItens().isEmpty()) {
            ItemVenda item = new ItemVenda();
            item.setQuantidade(1);
            item.setPrecoUnitario(BigDecimal.ZERO);
            item.setSubtotal(BigDecimal.ZERO);
            venda.adicionarItem(item);
        }

        prepararFormulario(model, venda);
        return "vendas/form";
    }

    /**
     * Salva uma venda.
     */
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("venda") Venda venda,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        try {
            vendaService.salvarVendaCompleta(venda);
            redirectAttributes.addFlashAttribute("sucesso", "Venda salva com sucesso.");
            return "redirect:/vendas";
        } catch (RuntimeException ex) {
            model.addAttribute("erro", ex.getMessage());
            prepararFormulario(model, venda);
            return "vendas/form";
        }
    }

    /**
     * Exclui uma venda existente.
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        vendaService.excluir(id);
        redirectAttributes.addFlashAttribute("sucesso", "Venda excluída com sucesso.");
        return "redirect:/vendas";
    }

    /**
     * Prepara os dados necessários para o formulário de venda.
     */
    private void prepararFormulario(Model model, Venda venda) {
        model.addAttribute("venda", venda);
        model.addAttribute("clientes", vendaService.listarClientesDaEmpresa());
        model.addAttribute("produtos", vendaService.listarProdutosDaEmpresa());
    }
}