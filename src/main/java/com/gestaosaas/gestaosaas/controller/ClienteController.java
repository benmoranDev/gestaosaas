package com.gestaosaas.gestaosaas.controller;



import com.gestaosaas.gestaosaas.entity.Cliente;
import com.gestaosaas.gestaosaas.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    /**
     * Exibe a lista de clientes.
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", service.listarTodos());
        return "clientes/lista";
    }

    /**
     * Exibe o formulário de novo cliente.
     */
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/form";
    }

    /**
     * Salva um novo cliente ou uma edição.
     */
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Cliente cliente,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "clientes/form";
        }

        service.salvar(cliente);
        redirectAttributes.addFlashAttribute("sucesso", "Cliente salvo com sucesso.");
        return "redirect:/clientes";
    }

    /**
     * Exibe o formulário de edição de um cliente.
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", service.buscarPorId(id));
        return "clientes/form";
    }

    /**
     * Exclui um cliente da empresa logada.
     */
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("sucesso", "Cliente excluído com sucesso.");
        return "redirect:/clientes";
    }
}