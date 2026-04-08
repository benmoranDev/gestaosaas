package com.gestaosaas.gestaosaas.controller;

import com.gestaosaas.gestaosaas.dto.PixCobrancaResponse;
import com.gestaosaas.gestaosaas.dto.PixStatusResponse;
import com.gestaosaas.gestaosaas.service.PixService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pix")
public class PixController {

    private final PixService pixService;

    public PixController(PixService pixService) {
        this.pixService = pixService;
    }

    // Gera a cobrança Pix para a venda.
    @PostMapping("/cobrancas/{vendaId}")
    public ResponseEntity<PixCobrancaResponse> gerarCobranca(@PathVariable Long vendaId) {
        return ResponseEntity.ok(pixService.gerarCobranca(vendaId));
    }

    // Consulta o status da cobrança Pix.
    @GetMapping("/cobrancas/{txid}")
    public ResponseEntity<PixStatusResponse> consultarStatus(@PathVariable String txid) {
        return ResponseEntity.ok(pixService.consultarStatus(txid));
    }
}