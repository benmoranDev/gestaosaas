package com.gestaosaas.gestaosaas.controller;

import com.gestaosaas.gestaosaas.dto.CadastroEmpresaForm;
import com.gestaosaas.gestaosaas.service.CadastroEmpresaService;
import com.gestaosaas.gestaosaas.service.OnboardingService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CadastroEmpresaController {

    private final OnboardingService onboardingService;

    public CadastroEmpresaController(OnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @GetMapping("/cadastro")
    public String exibirFormulario(Model model) {
        if (!model.containsAttribute("cadastroEmpresaForm")) {
            model.addAttribute("cadastroEmpresaForm", new CadastroEmpresaForm());
        }
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrarEmpresa(
            @Valid @ModelAttribute("cadastroEmpresaForm") CadastroEmpresaForm form,
            BindingResult bindingResult,
            Model model) {

        if (!form.senhasConferem()) {
            bindingResult.rejectValue(
                    "confirmacaoSenha",
                    "senha.nao.confere",
                    "As senhas não conferem."
            );
        }

        if (bindingResult.hasErrors()) {
            return "cadastro";
        }

        try {
            onboardingService.cadastrarEmpresaComAdmin(form);
            return "redirect:/login?cadastroSucesso";
        } catch (RuntimeException ex) {
            model.addAttribute("erro", ex.getMessage());
            return "cadastro";
        }
    }
}