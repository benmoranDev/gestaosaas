package com.gestaosaas.gestaosaas.repository;



import com.gestaosaas.gestaosaas.entity.DocumentoFiscal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional; // 👈 import obrigatório para Optional

public interface DocumentoFiscalRepository extends JpaRepository<DocumentoFiscal, Long> {

    Optional<DocumentoFiscal> findByVendaId(Long vendaId);
}