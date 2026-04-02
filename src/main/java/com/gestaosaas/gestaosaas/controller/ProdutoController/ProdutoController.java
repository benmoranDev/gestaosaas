package com.gestaosaas.gestaosaas.controller.ProdutoController;

import com.gestaosaas.gestaosaas.entity.Produto;
import com.gestaosaas.gestaosaas.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    /**
     * Exibe a lista de produtos.
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", service.listarTodos());
        return "produtos/lista";
    }

    /**
     * Exibe o formulário de novo produto.
     */
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("produto", new Produto());
        return "produtos/form";
    }

    /**
     * Salva um novo produto ou uma edição.
     */
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("produto") Produto produto,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "produtos/form";
        }

        service.salvar(produto);
        redirectAttributes.addFlashAttribute("sucesso", "Produto salvo com sucesso.");
        return "redirect:/produtos";
    }

    /**
     * Exibe o formulário de edição de um produto.
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("produto", service.buscarPorId(id));
        return "produtos/form";
    }

    /**
     * Exclui um produto da empresa logada.
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("sucesso", "Produto excluído com sucesso.");
        return "redirect:/produtos";
    }
}