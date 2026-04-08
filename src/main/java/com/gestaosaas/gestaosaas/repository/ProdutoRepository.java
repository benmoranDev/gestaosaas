package com.gestaosaas.gestaosaas.repository;


import com.gestaosaas.gestaosaas.entity.Empresa;
import com.gestaosaas.gestaosaas.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByEmpresa(Empresa empresa);

    Optional<Produto> findByIdAndEmpresa(Long id, Empresa empresa);

    long countByEmpresa(Empresa empresa);
}