package com.gestaosaas.gestaosaas.repository;

import com.gestaosaas.gestaosaas.entity.ContaReceber;
import com.gestaosaas.gestaosaas.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContaReceberRepository extends JpaRepository<ContaReceber, Long> {

    List<ContaReceber> findByEmpresa(Empresa empresa);

    Optional<ContaReceber> findByIdAndEmpresa(Long id, Empresa empresa);
}

