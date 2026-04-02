package com.gestaosaas.gestaosaas.controller.UsuarioController;

import com.gestaosaas.gestaosaas.entity.Perfil;
import com.gestaosaas.gestaosaas.entity.Usuario;
import com.gestaosaas.gestaosaas.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/*@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", service.listarTodos());
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("perfis", Perfil.values());
        return "usuarios/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Usuario usuario,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("perfis", Perfil.values());
            return "usuarios/form";
        }

        service.salvar(usuario);
        return "redirect:/usuarios";
    }
}

*/