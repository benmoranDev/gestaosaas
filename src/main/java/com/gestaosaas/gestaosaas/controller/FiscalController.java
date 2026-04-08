package com.gestaosaas.gestaosaas.controller;

import com.gestaosaas.gestaosaas.dto.DocumentoFiscalResponse;
import com.gestaosaas.gestaosaas.entity.DocumentoFiscal;
import com.gestaosaas.gestaosaas.service.FiscalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fiscal")
public class FiscalController {

    private final FiscalService fiscalService;

    public FiscalController(FiscalService fiscalService) {
        this.fiscalService = fiscalService;
    }

    @GetMapping("/venda/{vendaId}")
    public ResponseEntity<DocumentoFiscalResponse> buscarPorVenda(@PathVariable Long vendaId) {
        DocumentoFiscal documento = fiscalService.buscarPorVenda(vendaId);

        DocumentoFiscalResponse response = new DocumentoFiscalResponse();
        response.setId(documento.getId());
        response.setTipo(documento.getTipo().name());
        response.setStatus(documento.getStatus().name());
        response.setNumero(documento.getNumero());
        response.setSerie(documento.getSerie());
        response.setChaveAcesso(documento.getChaveAcesso());
        response.setProtocolo(documento.getProtocolo());
        response.setMotivoRejeicao(documento.getMotivoRejeicao());

        return ResponseEntity.ok(response);
    }
}
