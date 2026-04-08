package com.gestaosaas.gestaosaas.service;

import com.gestaosaas.gestaosaas.entity.DocumentoFiscal;
import com.gestaosaas.gestaosaas.entity.StatusFiscal;
import com.gestaosaas.gestaosaas.entity.TipoDocumentoFiscal;
import com.gestaosaas.gestaosaas.entity.Venda;
import com.gestaosaas.gestaosaas.repository.DocumentoFiscalRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class FiscalService {

    private final DocumentoFiscalRepository documentoFiscalRepository;
    private final VendaService vendaService;

    public FiscalService(DocumentoFiscalRepository documentoFiscalRepository,
                         VendaService vendaService) {
        this.documentoFiscalRepository = documentoFiscalRepository;
        this.vendaService = vendaService;
    }

    // Emissão fiscal inicial mockada para preparar o fluxo real.
    @Transactional
    public DocumentoFiscal emitirDocumentoParaVenda(Long vendaId) {
        Venda venda = vendaService.buscarPorId(vendaId);

        DocumentoFiscal documento = documentoFiscalRepository.findByVendaId(vendaId)
                .orElseGet(DocumentoFiscal::new);

        documento.setVenda(venda);

        // Regra inicial: PDV de mercadoria emite NFC-e.
        documento.setTipo(TipoDocumentoFiscal.NFCE);
        documento.setStatus(StatusFiscal.AUTORIZADO);
        documento.setNumero(String.valueOf(System.currentTimeMillis()).substring(6));
        documento.setSerie("1");
        documento.setChaveAcesso("NFE-MOCK-" + vendaId + "-" + System.currentTimeMillis());
        documento.setProtocolo("PROTOCOLO-MOCK-" + vendaId);
        documento.setXmlEnvio("<xml>mock-envio</xml>");
        documento.setXmlRetorno("<xml>mock-retorno-autorizado</xml>");
        documento.setEmitidoEm(LocalDateTime.now());

        documentoFiscalRepository.save(documento);

        venda.setTipoDocumentoFiscal(TipoDocumentoFiscal.NFCE);
        venda.setNumeroDocumentoFiscal(documento.getNumero());
        venda.setSerieDocumentoFiscal(documento.getSerie());
        venda.setChaveAcessoFiscal(documento.getChaveAcesso());
        venda.setProtocoloAutorizacaoFiscal(documento.getProtocolo());
        venda.setDataEmissaoFiscal(documento.getEmitidoEm());

        vendaService.salvar(venda);

        return documento;
    }

    public DocumentoFiscal buscarPorVenda(Long vendaId) {
        return documentoFiscalRepository.findByVendaId(vendaId)
                .orElseThrow(() -> new RuntimeException("Documento fiscal não encontrado para a venda."));
    }
}
