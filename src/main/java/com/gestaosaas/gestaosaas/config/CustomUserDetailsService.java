package com.gestaosaas.gestaosaas.config;

import com.gestaosaas.gestaosaas.entity.Usuario;
import com.gestaosaas.gestaosaas.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        System.out.println("Usuário autenticado: " + usuario.getEmail());
        System.out.println("Perfil carregado: " + usuario.getPerfil().name());
        System.out.println("Authority gerada: ROLE_" + usuario.getPerfil().name());

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name())))
                .disabled(!usuario.isAtivo())
                .build();
    }
}

