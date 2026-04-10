package com.gestaosaas.gestaosaas.service;

import com.gestaosaas.gestaosaas.dto.CadastroEmpresaForm;
import com.gestaosaas.gestaosaas.entity.Empresa;
import com.gestaosaas.gestaosaas.entity.Perfil;
import com.gestaosaas.gestaosaas.entity.Usuario;
import com.gestaosaas.gestaosaas.repository.EmpresaRepository;
import com.gestaosaas.gestaosaas.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class OnboardingService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public OnboardingService(EmpresaRepository empresaRepository,
                             UsuarioRepository usuarioRepository,
                             PasswordEncoder passwordEncoder) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void cadastrarEmpresaComAdmin(CadastroEmpresaForm form) {
        if (!form.senhasConferem()) {
            throw new RuntimeException("As senhas não conferem.");
        }

        if (usuarioRepository.findByEmail(form.getEmailAdministrador()).isPresent()) {
            throw new RuntimeException("Já existe um usuário com esse e-mail.");
        }

        if (empresaRepository.existsBySlug(form.getSlugEmpresa())) {
            throw new RuntimeException("Já existe uma empresa com esse slug.");
        }

        Empresa empresa = new Empresa();
        empresa.setNome(form.getNomeEmpresa());
        empresa.setSlug(form.getSlugEmpresa());
        empresa.setCnpj(form.getCnpj());
        empresa.setAtiva(true);

        empresaRepository.save(empresa);

        Usuario administrador = new Usuario();
        administrador.setNome(form.getNomeAdministrador());
        administrador.setEmail(form.getEmailAdministrador());
        administrador.setSenha(passwordEncoder.encode(form.getSenha()));
        administrador.setPerfil(Perfil.ADMIN);
        administrador.setAtivo(true);
        administrador.setEmpresa(empresa);

        usuarioRepository.save(administrador);
    }
}