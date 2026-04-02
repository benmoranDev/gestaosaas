package com.gestaosaas.gestaosaas.repository;

import com.gestaosaas.gestaosaas.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findBySlug(String slug);

    boolean existsBySlug(String slug);
}