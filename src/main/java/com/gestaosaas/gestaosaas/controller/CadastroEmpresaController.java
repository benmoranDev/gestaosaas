package com.gestaosaas.gestaosaas.controller;

import com.gestaosaas.gestaosaas.dto.CadastroEmpresaForm;
import com.gestaosaas.gestaosaas.service.CadastroEmpresaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller responsável pela tela pública de cadastro
 * de novas empresas na plataforma.
 */
@Controller
public class CadastroEmpresaController {

    private final CadastroEmpresaService cadastroEmpresaService;

    public CadastroEmpresaController(CadastroEmpresaService cadastroEmpresaService) {
        this.cadastroEmpresaService = cadastroEmpresaService;
    }

    /**
     * Exibe a tela de cadastro de empresa.
     */
    @GetMapping("/cadastro")
    public String exibirFormulario(Model model) {
        model.addAttribute("cadastroEmpresaForm", new CadastroEmpresaForm());
        return "auth/cadastro";
    }

    /**
     * Processa o envio do formulário de cadastro.
     */
    @PostMapping("/cadastro")
    public String cadastrar(@Valid @ModelAttribute CadastroEmpresaForm cadastroEmpresaForm,
                            BindingResult result,
                            Model model) {

        if (result.hasErrors()) {
            return "auth/cadastro";
        }

        try {
            cadastroEmpresaService.cadastrarNovaEmpresa(cadastroEmpresaForm);
            return "redirect:/login?cadastroSucesso";
        } catch (RuntimeException ex) {
            model.addAttribute("erro", ex.getMessage());
            return "auth/cadastro";
        }
    }
}