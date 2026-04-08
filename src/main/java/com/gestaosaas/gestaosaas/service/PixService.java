package com.gestaosaas.gestaosaas.service;

import com.gestaosaas.gestaosaas.config.PixProperties;
import com.gestaosaas.gestaosaas.dto.PixCobrancaResponse;
import com.gestaosaas.gestaosaas.dto.PixStatusResponse;
import com.gestaosaas.gestaosaas.dto.PixWebhookRequest;
import com.gestaosaas.gestaosaas.entity.PagamentoPix;
import com.gestaosaas.gestaosaas.entity.StatusPagamento;
import com.gestaosaas.gestaosaas.entity.Venda;
import com.gestaosaas.gestaosaas.repository.PagamentoPixRepository;
import com.gestaosaas.gestaosaas.service.gateway.PixGateway;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PixService {

    private final PagamentoPixRepository pagamentoPixRepository;
    private final VendaService vendaService;
    private final FiscalService fiscalService;
    private final PixGateway pixGateway;
    private final PixProperties pixProperties;

    public PixService(PagamentoPixRepository pagamentoPixRepository,
                      VendaService vendaService,
                      FiscalService fiscalService,
                      PixGateway pixGateway,
                      PixProperties pixProperties) {
        this.pagamentoPixRepository = pagamentoPixRepository;
        this.vendaService = vendaService;
        this.fiscalService = fiscalService;
        this.pixGateway = pixGateway;
        this.pixProperties = pixProperties;
    }

    // Gera uma nova cobrança Pix para a venda.
    @Transactional
    public PixCobrancaResponse gerarCobranca(Long vendaId) {
        Venda venda = vendaService.buscarPorId(vendaId);

        Optional<PagamentoPix> existente = pagamentoPixRepository.findByVendaId(vendaId);
        if (existente.isPresent() && existente.get().getStatus() == StatusPagamento.PAGO) {
            PagamentoPix pagamento = existente.get();
            return toCobrancaResponse(pagamento);
        }

        String txid = gerarTxid();
        BigDecimal valorVenda = obterValorDaVenda(venda);

        PixGateway.PixGatewayCobranca cobranca = pixGateway.criarCobranca(txid, valorVenda);

        PagamentoPix pagamento = existente.orElseGet(PagamentoPix::new);
        pagamento.setVenda(venda);
        pagamento.setTxid(cobranca.getTxid());
        pagamento.setValor(valorVenda);
        pagamento.setStatus(StatusPagamento.AGUARDANDO_PIX);
        pagamento.setQrCodeUrl(cobranca.getQrCodeUrl());
        pagamento.setPixCopiaECola(cobranca.getCopiaECola());
        pagamento.setExpiracaoEm(cobranca.getExpiracaoEm());
        pagamento.setRecebedorNome(pixProperties.getRecebedorNome());
        pagamento.setRecebedorChavePix(pixProperties.getChave());

        pagamentoPixRepository.save(pagamento);
        vendaService.marcarComoAguardandoPix(venda);

        return toCobrancaResponse(pagamento);
    }

    // Consulta o status atual do pagamento Pix.
    @Transactional
    public PixStatusResponse consultarStatus(String txid) {
        PagamentoPix pagamento = pagamentoPixRepository.findByTxid(txid)
                .orElseThrow(() -> new RuntimeException("Cobrança Pix não encontrada."));

        if (pagamento.getStatus() == StatusPagamento.PAGO) {
            return new PixStatusResponse(
                    pagamento.getTxid(),
                    pagamento.getStatus().name(),
                    pagamento.getEndToEndId(),
                    pagamento.getPagoEm()
            );
        }

        if (pagamento.getExpiracaoEm() != null && LocalDateTime.now().isAfter(pagamento.getExpiracaoEm())) {
            pagamento.setStatus(StatusPagamento.EXPIRADO);
            pagamentoPixRepository.save(pagamento);
        }

        PixGateway.PixGatewayStatus statusGateway = pixGateway.consultarStatus(txid);

        if (statusGateway.isPago()) {
            confirmarPagamentoInterno(
                    pagamento,
                    statusGateway.getEndToEndId(),
                    "Consulta ativa do status Pix"
            );
        }

        return new PixStatusResponse(
                pagamento.getTxid(),
                pagamento.getStatus().name(),
                pagamento.getEndToEndId(),
                pagamento.getPagoEm()
        );
    }

    // Processa a confirmação recebida pelo webhook do provedor Pix.
    @Transactional
    public void processarWebhook(PixWebhookRequest request, String tokenRecebido) {
        if (!pixGateway.validarTokenWebhook(tokenRecebido)) {
            throw new RuntimeException("Token do webhook inválido.");
        }

        PagamentoPix pagamento = pagamentoPixRepository.findByTxid(request.getTxid())
                .orElseThrow(() -> new RuntimeException("Cobrança Pix não encontrada para o txid informado."));

        confirmarPagamentoInterno(pagamento, request.getEndToEndId(), request.getPayloadBruto());
    }

    private void confirmarPagamentoInterno(PagamentoPix pagamento, String endToEndId, String payload) {
        if (pagamento.getStatus() == StatusPagamento.PAGO) {
            return;
        }

        pagamento.setStatus(StatusPagamento.PAGO);
        pagamento.setPagoEm(LocalDateTime.now());
        pagamento.setEndToEndId(endToEndId);
        pagamento.setPayloadWebhook(payload);

        pagamentoPixRepository.save(pagamento);

        Venda venda = pagamento.getVenda();
        vendaService.confirmarPagamentoPix(venda);

        // Dispara o processo fiscal após o pagamento confirmado.
        fiscalService.emitirDocumentoParaVenda(venda.getId());
    }

    private PixCobrancaResponse toCobrancaResponse(PagamentoPix pagamento) {
        return new PixCobrancaResponse(
                pagamento.getTxid(),
                pagamento.getValor(),
                pagamento.getQrCodeUrl(),
                pagamento.getPixCopiaECola(),
                pagamento.getExpiracaoEm(),
                pagamento.getStatus().name()
        );
    }

    private String gerarTxid() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 24).toUpperCase();
    }

    // Ajuste este método conforme o nome do campo total da sua Venda.
    private BigDecimal obterValorDaVenda(Venda venda) {
        try {
            return (BigDecimal) venda.getClass().getMethod("getTotal").invoke(venda);
        } catch (Exception e) {
            throw new RuntimeException("Ajuste o método obterValorDaVenda para o campo total da sua entidade Venda.");
        }
    }
}
