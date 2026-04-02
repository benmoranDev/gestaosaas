package com.gestaosaas.gestaosaas.controller;

import com.gestaosaas.gestaosaas.dto.EmpresaForm;
import com.gestaosaas.gestaosaas.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Controller responsável pela empresa atual
@Controller
@RequestMapping("/empresa")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    // Exibe o formulário da empresa atual
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String exibirFormulario(Model model) {
        model.addAttribute("empresaForm", empresaService.obterFormularioEmpresaAtual());
        return "empresa/form";
    }

    // Salva as alterações da empresa
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute EmpresaForm empresaForm,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            return "empresa/form";
        }

        try {
            empresaService.atualizarEmpresaAtual(empresaForm);
            model.addAttribute("sucesso", "Dados da empresa atualizados com sucesso.");
        } catch (RuntimeException ex) {
            model.addAttribute("erro", ex.getMessage());
        }

        return "empresa/form";
    }
}
