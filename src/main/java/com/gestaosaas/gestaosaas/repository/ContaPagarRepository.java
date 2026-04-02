package com.gestaosaas.gestaosaas.repository;

import com.gestaosaas.gestaosaas.entity.ContaPagar;
import com.gestaosaas.gestaosaas.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContaPagarRepository extends JpaRepository<ContaPagar, Long> {

    List<ContaPagar> findByEmpresa(Empresa empresa);

    Optional<ContaPagar> findByIdAndEmpresa(Long id, Empresa empresa);
}