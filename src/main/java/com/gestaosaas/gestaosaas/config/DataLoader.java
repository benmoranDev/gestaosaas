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

            if (usuarioRepository.findByEmail("admin@gestao.com").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNome("Administrador");
                admin.setEmail("admin@gestao.com");
                admin.setSenha(passwordEncoder.encode("123456"));
                admin.setPerfil(Perfil.ADMIN);
                admin.setAtivo(true);
                admin.setEmpresa(empresa);

                usuarioRepository.save(admin);
            }
        };


    }
}
