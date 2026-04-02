package com.gestaosaas.gestaosaas.repository;

import com.gestaosaas.gestaosaas.entity.Empresa;
import com.gestaosaas.gestaosaas.entity.Venda;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findByEmpresa(Empresa empresa);

    Optional<Venda> findByIdAndEmpresa(Long id, Empresa empresa);

    long countByEmpresa(Empresa empresa);


}