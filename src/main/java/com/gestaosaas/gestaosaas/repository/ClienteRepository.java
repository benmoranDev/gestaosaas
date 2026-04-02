package com.gestaosaas.gestaosaas.repository;


import com.gestaosaas.gestaosaas.entity.Cliente;
import com.gestaosaas.gestaosaas.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByEmpresa(Empresa empresa);

    Optional<Cliente> findByIdAndEmpresa(Long id, Empresa empresa);

    long countByEmpresa(Empresa empresa);
}

