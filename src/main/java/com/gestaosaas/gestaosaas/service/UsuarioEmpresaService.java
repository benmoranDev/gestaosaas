package com.gestaosaas.gestaosaas.service;

import com.gestaosaas.gestaosaas.dto.UsuarioEdicaoForm;
import com.gestaosaas.gestaosaas.dto.UsuarioForm;
import com.gestaosaas.gestaosaas.entity.Empresa;
import com.gestaosaas.gestaosaas.entity.Usuario;
import com.gestaosaas.gestaosaas.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioEmpresaService {

    private final UsuarioRepository usuarioRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioEmpresaService(UsuarioRepository usuarioRepository,
                                 AuthService authService,
                                 PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Lista os usuários da empresa atual.
     */
    public List<Usuario> listarUsuariosDaEmpresaAtual() {
        Empresa empresa = authService.usuarioLogado().getEmpresa();
        return usuarioRepository.findByEmpresa(empresa);
    }

    /**
     * Cadastra um novo usuário na empresa atual.
     */
    @Transactional
    public void cadastrarUsuario(UsuarioForm form) {
        if (usuarioRepository.existsByEmail(form.getEmail())) {
            throw new RuntimeException("Já existe um usuário cadastrado com esse email");
        }

        Empresa empresa = authService.usuarioLogado().getEmpresa();

        Usuario usuario = new Usuario();
        usuario.setNome(form.getNome());
        usuario.setEmail(form.getEmail());
        usuario.setSenha(passwordEncoder.encode(form.getSenha()));
        usuario.setPerfil(form.getPerfil());
        usuario.setAtivo(true);
        usuario.setEmpresa(empresa);

        usuarioRepository.save(usuario);
    }

    /**
     * Exclui um usuário da empresa atual.
     */
    @Transactional
    public void excluirUsuario(Long id) {
        Empresa empresa = authService.usuarioLogado().getEmpresa();

        Usuario usuario = usuarioRepository.findByIdAndEmpresa(id, empresa)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuarioRepository.delete(usuario);
    }

    /**
     * Busca um usuário da empresa atual e converte para formulário de edição.
     */
    public UsuarioEdicaoForm obterFormularioEdicao(Long id) {
        Empresa empresa = authService.usuarioLogado().getEmpresa();

        Usuario usuario = usuarioRepository.findByIdAndEmpresa(id, empresa)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        UsuarioEdicaoForm form = new UsuarioEdicaoForm();
        form.setId(usuario.getId());
        form.setNome(usuario.getNome());
        form.setEmail(usuario.getEmail());
        form.setPerfil(usuario.getPerfil());
        form.setAtivo(usuario.isAtivo());

        return form;
    }

    /**
     * Atualiza os dados de um usuário da empresa atual.
     */
    @Transactional
    public void atualizarUsuario(UsuarioEdicaoForm form) {
        Empresa empresa = authService.usuarioLogado().getEmpresa();

        Usuario usuario = usuarioRepository.findByIdAndEmpresa(form.getId(), empresa)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuarioRepository.existsByEmailAndIdNot(form.getEmail(), form.getId())) {
            throw new RuntimeException("Já existe outro usuário com esse email");
        }

        usuario.setNome(form.getNome());
        usuario.setEmail(form.getEmail());
        usuario.setPerfil(form.getPerfil());
        usuario.setAtivo(form.isAtivo());

        // Atualiza a senha somente se o campo foi preenchido
        if (form.getSenha() != null && !form.getSenha().isBlank()) {
            if (form.getSenha().length() < 6) {
                throw new RuntimeException("A nova senha deve ter pelo menos 6 caracteres");
            }
            usuario.setSenha(passwordEncoder.encode(form.getSenha()));
        }

        usuarioRepository.save(usuario);
    }
}