package com.gestaosaas.gestaosaas.repository;

import com.gestaosaas.gestaosaas.entity.Empresa;
import com.gestaosaas.gestaosaas.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    //Busca um usuário pelo email.
    Optional<Usuario> findByEmail(String email);

    //Verifica se já existe usuário com o email informado.
    boolean existsByEmail(String email);

    //Lista todos os usuários da empresa informada.
    List<Usuario> findByEmpresa(Empresa empresa);

    //Busca um usuário pelo ID e pela empresa.

    Optional<Usuario> findByIdAndEmpresa(Long id, Empresa empresa);

    boolean existsByEmailAndIdNot(String email, Long id);

}