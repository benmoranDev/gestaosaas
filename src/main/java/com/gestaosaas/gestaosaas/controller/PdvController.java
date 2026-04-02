package com.gestaosaas.gestaosaas.controller;

import com.gestaosaas.gestaosaas.dto.PdvFinalizarForm;
import com.gestaosaas.gestaosaas.dto.PdvItemForm;
import com.gestaosaas.gestaosaas.entity.*;
import com.gestaosaas.gestaosaas.service.ProdutoService;
import com.gestaosaas.gestaosaas.service.VendaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/pdv")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'OPERADOR')")
public class PdvController {

    private final VendaService vendaService;
    private final ProdutoService produtoService;
    private final ObjectMapper objectMapper;

    public PdvController(VendaService vendaService,
                         ProdutoService produtoService,
                         ObjectMapper objectMapper) {
        this.vendaService = vendaService;
        this.produtoService = produtoService;
        this.objectMapper = objectMapper;
    }

    /**
     * Exibe a tela principal do PDV.
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("clientes", vendaService.listarClientesDaEmpresa());
        model.addAttribute("produtos", produtoService.listarProdutosDisponiveisParaVenda());
        model.addAttribute("formasPagamento", FormaPagamento.values());
        model.addAttribute("pdvForm", new PdvFinalizarForm());
        return "pdv/index";
    }

    /**
     * Finaliza a venda enviada pelo carrinho do PDV.
     */
    @PostMapping("/finalizar")
    public String finalizarVenda(@ModelAttribute PdvFinalizarForm pdvForm,
                                 RedirectAttributes redirectAttributes) {
        try {
            List<PdvItemForm> itens = objectMapper.readValue(
                    pdvForm.getItensJson(),
                    new TypeReference<List<PdvItemForm>>() {}
            );

            if (itens == null || itens.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Adicione pelo menos um item ao carrinho.");
                return "redirect:/pdv";
            }

            Venda venda = new Venda();
            venda.setDataVenda(LocalDate.now());
            venda.setFormaPagamento(pdvForm.getFormaPagamento());
            venda.setValorRecebido(pdvForm.getValorRecebido());

            if (pdvForm.getClienteId() != null) {
                Cliente cliente = new Cliente();
                cliente.setId(pdvForm.getClienteId());
                venda.setCliente(cliente);
            }

            List<ItemVenda> itensVenda = new ArrayList<>();

            for (PdvItemForm itemForm : itens) {
                if (itemForm.getProdutoId() == null || itemForm.getQuantidade() == null || itemForm.getQuantidade() <= 0) {
                    continue;
                }

                Produto produto = new Produto();
                produto.setId(itemForm.getProdutoId());

                ItemVenda item = new ItemVenda();
                item.setProduto(produto);
                item.setQuantidade(itemForm.getQuantidade());

                itensVenda.add(item);
            }

            venda.setItens(itensVenda);

            Venda vendaSalva = vendaService.salvarVendaCompleta(venda);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Venda finalizada com sucesso. Total: R$ " + vendaSalva.getValorTotal()
            );

            return "redirect:/pdv";

        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/pdv";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erro", "Não foi possível finalizar a venda.");
            return "redirect:/pdv";
        }
    }
}