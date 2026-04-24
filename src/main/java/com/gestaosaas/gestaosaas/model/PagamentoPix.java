package com.gestaosaas.gestaosaas.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class PagamentoPix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cada cobrança Pix pertence a uma venda.
    @OneToOne(optional = false)
    @JoinColumn(name = "venda_id", nullable = false, unique = true)
    private Venda venda;

    // Identificador único da transação no Pix.
    @Column(nullable = false, unique = true, length = 100)
    private String txid;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusPagamento status = StatusPagamento.PENDENTE;

    @Column(length = 1000)
    private String qrCodeUrl;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String pixCopiaECola;

    private LocalDateTime criadoEm;
    private LocalDateTime expiracaoEm;
    private LocalDateTime pagoEm;

    @Column(length = 100)
    private String endToEndId;

    @Column(length = 150)
    private String recebedorNome;

    @Column(length = 150)
    private String recebedorChavePix;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String payloadWebhook;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getPixCopiaECola() {
        return pixCopiaECola;
    }

    public void setPixCopiaECola(String pixCopiaECola) {
        this.pixCopiaECola = pixCopiaECola;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getExpiracaoEm() {
        return expiracaoEm;
    }

    public void setExpiracaoEm(LocalDateTime expiracaoEm) {
        this.expiracaoEm = expiracaoEm;
    }

    public LocalDateTime getPagoEm() {
        return pagoEm;
    }

    public void setPagoEm(LocalDateTime pagoEm) {
        this.pagoEm = pagoEm;
    }

    public String getEndToEndId() {
        return endToEndId;
    }

    public void setEndToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
    }

    public String getRecebedorNome() {
        return recebedorNome;
    }

    public void setRecebedorNome(String recebedorNome) {
        this.recebedorNome = recebedorNome;
    }

    public String getRecebedorChavePix() {
        return recebedorChavePix;
    }

    public void setRecebedorChavePix(String recebedorChavePix) {
        this.recebedorChavePix = recebedorChavePix;
    }

    public String getPayloadWebhook() {
        return payloadWebhook;
    }

    public void setPayloadWebhook(String payloadWebhook) {
        this.payloadWebhook = payloadWebhook;
    }
}
