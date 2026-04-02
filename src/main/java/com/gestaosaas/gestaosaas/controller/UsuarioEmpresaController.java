package com.gestaosaas.gestaosaas.controller;

import com.gestaosaas.gestaosaas.dto.UsuarioEdicaoForm;
import com.gestaosaas.gestaosaas.dto.UsuarioForm;
import com.gestaosaas.gestaosaas.entity.Perfil;
import com.gestaosaas.gestaosaas.service.UsuarioEmpresaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pela gestão de usuários da empresa.
 */
@Controller
@RequestMapping("/empresa/usuarios")
public class UsuarioEmpresaController {

    private final UsuarioEmpresaService usuarioEmpresaService;

    public UsuarioEmpresaController(UsuarioEmpresaService usuarioEmpresaService) {
        this.usuarioEmpresaService = usuarioEmpresaService;
    }

    /**
     * Lista os usuários da empresa atual.
     * Apenas ADMIN pode acessar.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioEmpresaService.listarUsuariosDaEmpresaAtual());
        return "usuarios/lista";
    }

    /**
     * Exibe o formulário de novo usuário.
     * Apenas ADMIN pode acessar.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("usuarioForm", new UsuarioForm());
        model.addAttribute("perfis", Perfil.values());
        return "usuarios/form";
    }

    /**
     * Salva um novo usuário.
     * Apenas ADMIN pode cadastrar usuário.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute UsuarioForm usuarioForm,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("perfis", Perfil.values());
            return "usuarios/form";
        }

        try {
            usuarioEmpresaService.cadastrarUsuario(usuarioForm);
            return "redirect:/empresa/usuarios";
        } catch (RuntimeException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("perfis", Perfil.values());
            return "usuarios/form";
        }
    }

    /**
     * Exibe o formulário de edição de usuário.
     * Apenas ADMIN pode editar.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuarioEdicaoForm", usuarioEmpresaService.obterFormularioEdicao(id));
        model.addAttribute("perfis", Perfil.values());
        return "usuarios/editar";
    }

    /**
     * Atualiza o usuário.
     * Apenas ADMIN pode atualizar.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/atualizar")
    public String atualizar(@Valid @ModelAttribute UsuarioEdicaoForm usuarioEdicaoForm,
                            BindingResult result,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("perfis", Perfil.values());
            return "usuarios/editar";
        }

        try {
            usuarioEmpresaService.atualizarUsuario(usuarioEdicaoForm);
            return "redirect:/empresa/usuarios";
        } catch (RuntimeException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("perfis", Perfil.values());
            return "usuarios/editar";
        }
    }

    /**
     * Exclui o usuário.
     * Apenas ADMIN pode excluir.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        usuarioEmpresaService.excluirUsuario(id);
        return "redirect:/empresa/usuarios";
    }
}