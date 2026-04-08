package com.gestaosaas.gestaosaas.config;

import com.gestaosaas.gestaosaas.entity.Empresa;
import com.gestaosaas.gestaosaas.entity.Perfil;
import com.gestaosaas.gestaosaas.entity.Usuario;
import com.gestaosaas.gestaosaas.repository.EmpresaRepository;
import com.gestaosaas.gestaosaas.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner carregarDados(EmpresaRepository empresaRepository,
                                           UsuarioRepository usuarioRepository,
                                           PasswordEncoder passwordEncoder) {
        return args -> {
            Empresa empresa = empresaRepository.findBySlug("empresa-demo")
                    .orElseGet(() -> {
                        Empresa e = new Empresa();
                        e.setNome("Empresa Demo");
                        e.setSlug("empresa-demo");
                        e.setAtiva(true);
                        return empresaRepository.save(e);
                    });

            criarUsuario(usuarioRepository, passwordEncoder, empresa,
                    "Administrador",    "admin@gestao.com",    "123456", Perfil.ADMIN);

            criarUsuario(usuarioRepository, passwordEncoder, empresa,
                    "Maria Caixa",      "maria@gestao.com",    "123456", Perfil.OPERADOR);

            criarUsuario(usuarioRepository, passwordEncoder, empresa,
                    "Ana Gerente",      "ana@gestao.com",      "123456", Perfil.GERENTE);
        };
    }

    private void criarUsuario(UsuarioRepository repo,
                              PasswordEncoder encoder,
                              Empresa empresa,
                              String nome,
                              String email,
                              String senha,
                              Perfil perfil) {
        if (repo.findByEmail(email).isEmpty()) {
            Usuario u = new Usuario();
            u.setNome(nome);
            u.setEmail(email);
            u.setSenha(encoder.encode(senha));
            u.setPerfil(perfil);
            u.setAtivo(true);
            u.setEmpresa(empresa);
            repo.save(u);
        }
    }
}