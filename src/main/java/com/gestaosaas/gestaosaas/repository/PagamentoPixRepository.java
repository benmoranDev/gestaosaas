package com.gestaosaas.gestaosaas.repository;

import com.gestaosaas.gestaosaas.entity.PagamentoPix;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagamentoPixRepository extends JpaRepository<PagamentoPix, Long> {

    Optional<PagamentoPix> findByTxid(String txid);

    Optional<PagamentoPix> findByVendaId(Long vendaId);
}
