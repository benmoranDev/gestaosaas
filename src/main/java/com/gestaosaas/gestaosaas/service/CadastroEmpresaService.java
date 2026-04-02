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

//Serviço responsável pelo cadastro de novas empresase criação do usuário administrador inicial.

@Service
public class CadastroEmpresaService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CadastroEmpresaService(EmpresaRepository empresaRepository,
                                  UsuarioRepository usuarioRepository,
                                  PasswordEncoder passwordEncoder) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Cadastra uma nova empresa no sistema e cria o usuário administrador vinculado a ela.
    // Tudo acontece na mesma transação. Se alguma parte falhar, nada será salvo.
     
    @Transactional
    public void cadastrarNovaEmpresa(CadastroEmpresaForm form) {

        // Valida se já existe empresa com o mesmo slug
        if (empresaRepository.existsBySlug(form.getSlugEmpresa())) {
            throw new RuntimeException("Já existe uma empresa cadastrada com esse slug");
        }

        // Valida se já existe usuário com o mesmo email
        if (usuarioRepository.existsByEmail(form.getEmailAdministrador())) {
            throw new RuntimeException("Já existe um usuário cadastrado com esse email");
        }

        // Cria a empresa
        Empresa empresa = new Empresa();
        empresa.setNome(form.getNomeEmpresa());
        empresa.setSlug(form.getSlugEmpresa());
        empresa.setAtiva(true);

        // Salva a empresa no banco
        Empresa empresaSalva = empresaRepository.save(empresa);

        // Cria o usuário administrador inicial da empresa
        Usuario administrador = new Usuario();
        administrador.setNome(form.getNomeAdministrador());
        administrador.setEmail(form.getEmailAdministrador());
        administrador.setSenha(passwordEncoder.encode(form.getSenha()));
        administrador.setPerfil(Perfil.ADMIN);
        administrador.setAtivo(true);
        administrador.setEmpresa(empresaSalva);

        // Salva o usuário administrador no banco
        usuarioRepository.save(administrador);
    }
}
